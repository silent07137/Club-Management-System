<template>
    <div style="padding: 20px;">
        <el-card>
            <template #header>
                <div style="font-weight: bold; font-size: 18px;">⚖️ 新社团开办审批</div>
            </template>

            <el-table :data="pendingClubs" border stripe v-loading="loading" style="width: 100%">
                <el-table-column prop="clubId" label="申请编号" width="100" />
                <el-table-column prop="name" label="拟办社团名称" width="180" />
                <el-table-column prop="description" label="社团简介" show-overflow-tooltip />
                <el-table-column prop="leaderId" label="申请人ID" width="100" />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="scope">
                        <el-button type="success" size="small" @click="handleApprove(scope.row)">通过</el-button>
                        <el-button type="danger" size="small" @click="handleReject(scope.row)">拒绝</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-empty v-if="pendingClubs.length === 0 && !loading" description="暂无待处理的开办申请" />
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const pendingClubs = ref([])
const loading = ref(false)

// 🚩 获取待审核列表 (status = 0)
const loadPending = async () => {
    loading.value = true
    try {
        const res = await request.get('/club/list/pending')
        if (res.code === 200) {
            pendingClubs.value = res.data
        }
    } catch (error) {
        console.error('获取列表失败:', error)
    } finally {
        loading.value = false
    }
}

// 🚩 执行审批操作
const handleApprove = (row) => {
    ElMessageBox.confirm(
        `确定准许【${row.name}】开办吗？通过后该申请人将自动晋升为社长。`,
        '审批确认',
        { confirmButtonText: '通过', cancelButtonText: '取消', type: 'success' }
    ).then(async () => {
        // 这里的参数名根据后端控制器的接收方式调整
        const res = await request.post('/club/approve', {
            clubId: row.clubId, // 🚩 注意：这里使用你在数据库截图中展示的 club_id
            userId: row.leaderId
        })

        if (res.code === 200) {
            ElMessage.success('审批成功，社团已激活')
            loadPending() // 刷新列表
        }
    }).catch(() => { })
}

const handleReject = (row) => {
  ElMessageBox.prompt('请输入拒绝理由', '驳回申请', {
    confirmButtonText: '确定驳回',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '理由不能为空',
  }).then(async ({ value }) => {
    // 调用拒绝接口
    const res = await request.post('/club/reject', { 
      clubId: row.clubId, 
      reason: value 
    })
    
    if (res.code === 200) {
      ElMessage.error('申请已驳回') // 这里的颜色建议用 error (红色) 对应拒绝动作
      loadPending() // 刷新列表
    }
  }).catch(() => {})
}

onMounted(() => {
    loadPending()
})
</script>

<style scoped>
.el-card {
    margin-top: 10px;
    border-radius: 8px;
}
</style>