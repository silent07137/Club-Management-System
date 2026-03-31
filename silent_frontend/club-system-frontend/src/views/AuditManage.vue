<template>
  <div class="audit-container">
    <h2 style="margin-bottom: 20px; color: #67C23A;">⚖️ 入社审批中心</h2>
    
    <el-table :data="applyList" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="memberId" label="申请编号" width="100" />
      <el-table-column prop="userName" label="申请人" /> <el-table-column prop="clubName" label="申请社团" />
      <el-table-column prop="createTime" label="申请时间" />
      
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button 
            type="success" 
            size="small" 
            @click="handleAudit(scope.row.memberId, 1)"
          >准许入社</el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleAudit(scope.row.memberId, 2)"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const applyList = ref([])

const loadApplyList = async () => {
  loading.value = true
  try {
    const res = await request.get('/club/member/list/pending')
    if (res.code == 200) {
      applyList.value = res.data
    }
  } catch (error) {
    console.error("加载失败", error)
  } finally {
    loading.value = false
  }
}

const handleAudit = async (id, status) => {
  try {
    const res = await request.post('/club/member/audit', {
      memberId: id,
      joinStatus: status
    })
    if (res.code == 200) {
      ElMessage.success("处理成功！")
      loadApplyList()
    }
  } catch (error) {
    ElMessage.error("系统异常")
  }
}

onMounted(() => {
  loadApplyList()
})
</script>