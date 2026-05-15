<template>
    <div style="padding: 20px;">
        <el-card>
            <template #header>
                <div style="font-weight: bold; font-size: 18px;">⚖️ 新社团开办审批</div>
            </template>

            <el-tabs v-model="activeTab">
                <el-tab-pane label="待审核" name="pending">
                    <el-table :data="pendingClubs" border stripe v-loading="loadingPending" style="width: 100%">
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

                    <el-empty v-if="pendingClubs.length === 0 && !loadingPending" description="暂无待处理的开办申请" />
                </el-tab-pane>

                <el-tab-pane label="已驳回" name="rejected">
                    <el-table :data="rejectedClubs" border stripe v-loading="loadingRejected" style="width: 100%">
                        <el-table-column prop="clubId" label="申请编号" width="100" />
                        <el-table-column prop="name" label="社团名称" width="180" />
                        <el-table-column prop="leaderId" label="申请人ID" width="100" />
                        <el-table-column prop="rejectReason" label="驳回理由" show-overflow-tooltip />
                        <el-table-column prop="createTime" label="申请时间" width="180" />
                    </el-table>

                    <el-empty v-if="rejectedClubs.length === 0 && !loadingRejected" description="暂无已驳回的申请" />
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('pending')
const pendingClubs = ref([])
const rejectedClubs = ref([])
const loadingPending = ref(false)
const loadingRejected = ref(false)

const loadPending = async () => {
    loadingPending.value = true
    try {
        const res = await request.get('/club/list/pending')
        if (res.code === 200) {
            pendingClubs.value = res.data
        }
    } catch (error) {
        console.error('获取待审核列表失败:', error)
    } finally {
        loadingPending.value = false
    }
}

const loadRejected = async () => {
    loadingRejected.value = true
    try {
        const res = await request.get('/club/list/rejected')
        if (res.code === 200) {
            rejectedClubs.value = res.data
        }
    } catch (error) {
        console.error('获取已驳回列表失败:', error)
    } finally {
        loadingRejected.value = false
    }
}

const refreshLists = async () => {
    await Promise.all([loadPending(), loadRejected()])
}

const handleApprove = (row) => {
    ElMessageBox.confirm(
        `确定准许【${row.name}】开办吗？通过后该申请人将自动晋升为社长。`,
        '审批确认',
        { confirmButtonText: '通过', cancelButtonText: '取消', type: 'success' }
    ).then(async () => {
        const res = await request.post('/club/approve', {
            clubId: row.clubId,
            userId: row.leaderId
        })
        if (res.code === 200) {
            ElMessage.success('审批成功，社团已激活')
            refreshLists()
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
        const res = await request.post('/club/reject', {
            clubId: row.clubId,
            reason: value
        })

        if (res.code === 200) {
            ElMessage.success('申请已驳回')
            refreshLists()
            activeTab.value = 'rejected'
        }
    }).catch(() => { })
}

onMounted(() => {
    refreshLists()
})
</script>

<style scoped>
.el-card {
    margin-top: 10px;
    border-radius: 8px;
}
</style>
