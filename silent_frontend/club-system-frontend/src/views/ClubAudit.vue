<template>
    <div class="page-shell">
        <el-card class="page-card" shadow="never">
            <template #header>
                <div class="page-header">
                    <div class="title-block">
                        <el-icon class="title-icon"><OfficeBuilding /></el-icon>
                        <div>
                            <h2>新社团开办审批</h2>
                            <p>审核社团创建申请，并查看已驳回记录。</p>
                        </div>
                    </div>
                    <el-badge :value="pendingClubs.length" :hidden="pendingClubs.length === 0" type="primary">
                        <el-button :loading="loadingPending || loadingRejected" @click="refreshLists">
                            <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
                            刷新
                        </el-button>
                    </el-badge>
                </div>
            </template>

            <el-tabs v-model="activeTab">
                <el-tab-pane label="待审核" name="pending">
                    <el-table :data="pendingClubs" border stripe v-loading="loadingPending" style="width: 100%">
                        <el-table-column prop="clubId" width="110">
                            <template #header>
                                <span class="column-header"><el-icon><Tickets /></el-icon><span>申请编号</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="name" width="180">
                            <template #header>
                                <span class="column-header"><el-icon><OfficeBuilding /></el-icon><span>拟办社团名称</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="description" show-overflow-tooltip>
                            <template #header>
                                <span class="column-header"><el-icon><Document /></el-icon><span>社团简介</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="leaderId" width="110">
                            <template #header>
                                <span class="column-header"><el-icon><UserFilled /></el-icon><span>申请人ID</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column width="150" fixed="right">
                            <template #header>
                                <span class="column-header"><el-icon><Operation /></el-icon><span>操作</span></span>
                            </template>
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
                        <el-table-column prop="clubId" width="110">
                            <template #header>
                                <span class="column-header"><el-icon><Tickets /></el-icon><span>申请编号</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="name" width="180">
                            <template #header>
                                <span class="column-header"><el-icon><OfficeBuilding /></el-icon><span>社团名称</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="leaderId" width="110">
                            <template #header>
                                <span class="column-header"><el-icon><UserFilled /></el-icon><span>申请人ID</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="rejectReason" show-overflow-tooltip>
                            <template #header>
                                <span class="column-header"><el-icon><CircleClose /></el-icon><span>驳回理由</span></span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createTime" width="180">
                            <template #header>
                                <span class="column-header"><el-icon><Clock /></el-icon><span>申请时间</span></span>
                            </template>
                        </el-table-column>
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
import { OfficeBuilding, Refresh, Tickets, UserFilled, Operation, Document, CircleClose, Clock } from '@element-plus/icons-vue'

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
.page-shell {
    padding: 20px;
}

.page-card {
    margin-top: 10px;
    border-radius: 14px;
}

.page-header {
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
    background: linear-gradient(135deg, #f56c6c, #f78989);
    color: #fff;
    font-size: 22px;
    flex: 0 0 auto;
}

.title-block h2 {
    margin: 0 0 4px;
    color: #1f2d3d;
}

.title-block p {
    margin: 0;
    color: #606266;
}

.column-header {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-weight: 600;
}

@media (max-width: 768px) {
    .page-header {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
