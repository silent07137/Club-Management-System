<template>
  <div class="page-shell">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><Calendar /></el-icon>
            <div>
              <h2>全局活动管理中心</h2>
              <p>平台级活动管理，仅管理员可维护全局活动信息。</p>
            </div>
          </div>
          <div class="header-actions">
            <el-badge :value="activityList.length" :hidden="activityList.length === 0" type="primary">
              <el-button :loading="loading" @click="loadData">
                <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
                刷新活动
              </el-button>
            </el-badge>
            <el-button type="primary" size="small" @click="handleAdd">
              <el-icon style="margin-right: 4px;"><Plus /></el-icon>
              新增活动
            </el-button>
          </div>
        </div>
      </template>

      <div class="toolbar-row">
        <el-input v-model="searchTitle" placeholder="请输入活动名称搜索" class="search-input" clearable @clear="loadData">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="全部状态" class="status-select" clearable @clear="loadData">
          <el-option label="全部状态" :value="''" />
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已结束" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="activityList" stripe style="width: 100%" border>
        <el-table-column prop="activityId" width="90">
          <template #header>
            <span class="column-header"><el-icon><Tickets /></el-icon><span>活动ID</span></span>
          </template>
        </el-table-column>
        <el-table-column prop="title">
          <template #header>
            <span class="column-header"><el-icon><Calendar /></el-icon><span>活动名称</span></span>
          </template>
        </el-table-column>
        <el-table-column prop="location">
          <template #header>
            <span class="column-header"><el-icon><Location /></el-icon><span>地点</span></span>
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template #header>
            <span class="column-header"><el-icon><Tickets /></el-icon><span>积分</span></span>
          </template>
          <template #default="scope">
            <el-tag type="warning" effect="plain">+{{ scope.row.pointsReward || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template #header>
            <span class="column-header"><el-icon><Flag /></el-icon><span>状态</span></span>
          </template>
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column width="240">
          <template #header>
            <span class="column-header"><el-icon><Operation /></el-icon><span>操作</span></span>
          </template>
          <template #default="scope">
            <el-button size="small" link type="primary" @click="handleEdit(scope.row)">
              <el-icon><EditPen /></el-icon>
              编辑
            </el-button>
            <el-button size="small" link type="success" @click="handleSetStatus(scope.row.activityId, 1)" v-if="scope.row.status !== 1">
              <el-icon><Flag /></el-icon>
              设为进行中
            </el-button>
            <el-button size="small" link type="warning" @click="handleSetStatus(scope.row.activityId, 2)" v-if="scope.row.status !== 2">
              <el-icon><Flag /></el-icon>
              设为已结束
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(scope.row.activityId)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.activityId ? '编辑活动' : '新增活动'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="活动名称">
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="积分奖励">
          <el-input-number v-model="form.pointsReward" :min="0" :max="100" />
          <span class="reward-tip">分</span>
        </el-form-item>
          <el-form-item label="活动状态">
            <el-radio-group v-model="form.status">
              <el-radio :value="0">未开始</el-radio>
              <el-radio :value="1">进行中</el-radio>
              <el-radio :value="2">已结束</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Calendar, Search, Refresh, Plus, Tickets, Location, Flag, Operation, EditPen, Delete } from '@element-plus/icons-vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchTitle = ref('')
const statusFilter = ref('')
const activityList = ref([])
const dialogVisible = ref(false)
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/activity/list', {
      params: {
        title: searchTitle.value,
        status: statusFilter.value === '' ? undefined : statusFilter.value
      }
    }) 
    if (res.code === 200) {
      activityList.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = (id) => {
  console.log("👉 点击了删除按钮，当前行的 ID 是：", id)

  if (!id) {
    ElMessage.error('无法获取该数据的ID，请按 F12 检查字段名！')
    return
  }

  ElMessageBox.confirm('确定要删除这个活动吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await request.delete(`/activity/delete/${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功！')
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const getStatusLabel = (status) => {
  if (status === 1) return '进行中'
  if (status === 2) return '已结束'
  return '未开始'
}

const getStatusTag = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return 'warning'
}

const form = reactive({
  title: '',
  location: '',
  pointsReward: 0,
  status: 0,
  clubId: 1,
  startTime: '2026-05-01 10:00:00',
  endTime: '2026-05-01 12:00:00'
})

const handleAdd = () => {
  form.activityId = undefined
  form.title = ''
  form.location = ''
  form.pointsReward = 0
  form.status = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  form.pointsReward = row.pointsReward || 0
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.title || !form.location) {
    ElMessage.warning('名称和地点不能为空哦！')
    return
  }
  try {
    let res;
    if (form.activityId) {
      res = await request.put('/activity/update', form)
    } else {
      res = await request.post('/activity/add', form)
    }

    if (res.code === 200) {
      ElMessage.success(form.activityId ? '修改成功！' : '新增成功！')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
  }
}

const handleSetStatus = async (id, status) => {
  try {
    const res = await request.put('/activity/status', { id, status })
    if (res.code === 200) {
      ElMessage.success(res.message || '状态已更新')
      loadData()
    } else {
      ElMessage.error(res.message || '状态更新失败')
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-shell {
  padding: 20px;
}

.page-card {
  border-radius: 14px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #67c23a, #95d475);
  color: #fff;
  font-size: 22px;
  flex: 0 0 auto;
}

.page-title h2 {
  margin: 0 0 4px;
  color: #1f2d3d;
}

.page-title p {
  margin: 0;
  color: #606266;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toolbar-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.search-input {
  width: 280px;
}

.column-header {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.reward-tip {
  margin-left: 8px;
  color: #909399;
}

@media (max-width: 768px) {
  .page-header,
  .toolbar-row,
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
