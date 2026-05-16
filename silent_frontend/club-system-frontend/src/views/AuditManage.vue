<template>
  <div class="audit-container">
    <el-card class="header-card" shadow="never">
      <div class="header-row">
        <div class="title-block">
          <el-icon class="title-icon"><Stamp /></el-icon>
          <div>
            <h2>入社审批中心</h2>
            <p>集中处理所有待审核的入社申请，减少来回切页。</p>
          </div>
        </div>
        <div class="header-actions">
          <el-badge :value="applyList.length" :hidden="applyList.length === 0" type="primary">
            <el-button :loading="loading" @click="loadApplyList">
              <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
              刷新
            </el-button>
          </el-badge>
        </div>
      </div>
    </el-card>

    <el-alert
      :title="`当前共有 ${applyList.length} 条待审批申请`"
      type="info"
      show-icon
      :closable="false"
      style="margin-bottom: 16px;"
    />

    <el-table
      :data="applyList"
      border
      stripe
      style="width: 100%"
      v-loading="loading"
      empty-text="当前没有待审批的入社申请"
    >
      <el-table-column prop="memberId" width="110">
        <template #header>
          <span class="column-header"><el-icon><Tickets /></el-icon><span>申请编号</span></span>
        </template>
      </el-table-column>
      <el-table-column prop="userName" min-width="140">
        <template #header>
          <span class="column-header"><el-icon><UserFilled /></el-icon><span>申请人</span></span>
        </template>
      </el-table-column>
      <el-table-column prop="clubName" min-width="160">
        <template #header>
          <span class="column-header"><el-icon><OfficeBuilding /></el-icon><span>申请社团</span></span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" min-width="180">
        <template #header>
          <span class="column-header"><el-icon><Clock /></el-icon><span>申请时间</span></span>
        </template>
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>

      <el-table-column width="220" fixed="right">
        <template #header>
          <span class="column-header"><el-icon><Operation /></el-icon><span>操作</span></span>
        </template>
        <template #default="scope">
          <el-button
            type="success"
            size="small"
            :loading="processingId === scope.row.memberId && processingAction === 1"
            @click="handleAudit(scope.row.memberId, 1)"
          >
            通过
          </el-button>
          <el-button
            type="danger"
            size="small"
            :loading="processingId === scope.row.memberId && processingAction === 2"
            @click="handleAudit(scope.row.memberId, 2)"
          >
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Stamp, Refresh, Tickets, UserFilled, OfficeBuilding, Clock, Operation } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const applyList = ref([])
const processingId = ref(null)
const processingAction = ref(null)

const formatTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

const loadApplyList = async () => {
  loading.value = true
  try {
    const res = await request.get('/club/member/list/pending')
    if (res.code === 200) {
      applyList.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取审批列表失败')
    }
  } catch (error) {
    ElMessage.error('获取审批列表失败')
  } finally {
    loading.value = false
  }
}

const handleAudit = async (id, status) => {
  const actionText = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${actionText}这条入社申请吗？`, '审批确认', {
      confirmButtonText: actionText,
      cancelButtonText: '取消',
      type: status === 1 ? 'success' : 'warning'
    })

    processingId.value = id
    processingAction.value = status
    const res = await request.post('/club/member/audit', {
      memberId: id,
      joinStatus: status
    })
    if (res.code === 200) {
      ElMessage.success(res.message || '处理成功')
      await loadApplyList()
    } else {
      ElMessage.error(res.message || '处理失败')
    }
  } catch (error) {
    if (error === 'cancel' || error === 'close') return
    ElMessage.error('系统异常')
  } finally {
    processingId.value = null
    processingAction.value = null
  }
}

onMounted(() => {
  loadApplyList()
})
</script>

<style scoped>
.audit-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 16px;
  border-radius: 14px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.title-block {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff, #79bbff);
  color: #fff;
  font-size: 22px;
  flex: 0 0 auto;
}

.title-block h2 {
  margin: 0 0 8px;
  color: #1f2d3d;
}

.title-block p {
  margin: 0;
  color: #606266;
}

.header-actions {
  display: flex;
  align-items: center;
}

.column-header {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
