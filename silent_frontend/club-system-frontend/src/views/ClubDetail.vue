<template>
    <div class="club-detail-container" v-loading="loading">
        <div class="header-box">
            <el-page-header @back="goBack">
                <template #content>
                    <span class="header-title"> {{ club.name }} - 社团主页 </span>
                </template>
                <template #extra>
                    <el-button v-if="isPresident" type="danger" @click="handleDisbandClub">
                        解散社团
                    </el-button>
                    <el-button v-else type="warning" plain @click="handleQuitClub">
                        退出社团
                    </el-button>
                </template>
            </el-page-header>
        </div>

        <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="8">
                <el-card shadow="always" class="info-card">
                    <div class="club-logo">
                        <el-avatar :size="100"
                            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                    </div>
                    <h2 class="text-center">{{ club.name }}</h2>
                    <el-divider />
                    <div class="detail-item">
                        <el-icon>
                            <InfoFilled />
                        </el-icon>
                        <span class="label">社团简介：</span>
                        <p class="content">{{ club.description || '这个社团很神秘，还没有简介...' }}</p>
                    </div>
                    <div class="detail-item">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        <span class="label">成立时间：</span>
                        <span class="content">{{ club.createTime }}</span>
                    </div>
                    <div class="detail-item">
                        <el-icon>
                            <User />
                        </el-icon>
                        <span class="label">当前身份：</span>
                        <el-tag type="success">正式成员</el-tag>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="16">
                <el-tabs type="border-card">
                    <el-tab-pane label="📢 最新公告">
                        <el-timeline>
                            <el-timeline-item timestamp="2024-03-30" placement="top">
                                <el-card>
                                    <h4>本周社团例会通知</h4>
                                    <p>请全体成员于周五下午 4 点在社团活动教室集合。</p>
                                </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="2024-03-25" placement="top">
                                <el-card>
                                    <h4>新成员欢迎仪式</h4>
                                    <p>欢迎所有新加入的小伙伴！</p>
                                </el-card>
                            </el-timeline-item>
                        </el-timeline>
                    </el-tab-pane>
                    <el-tab-pane label="🎉 社团活动" name="activities">
                        <div style="margin-bottom: 20px;" v-if="isPresident">
                            <el-button type="primary" @click="showAddActivityDialog = true">
                                + 发布新活动
                            </el-button>
                        </div>

                        <el-timeline v-if="activityList.length > 0">
                            <el-timeline-item v-for="activity in activityList" :key="activity.activityId"
                                :timestamp="activity.startTime ? activity.startTime.replace('T', ' ') : ''"
                                placement="top" type="primary">
                                <el-card>
                                    <h3 style="margin-top: 0">{{ activity.title }}</h3>
                                    <p><strong>📍 地点：</strong>{{ activity.location }}</p>
                                    <p><strong>📝 详情：</strong>{{ activity.description }}</p>
                                    <p style="color: #909399; font-size: 13px;">
                                        结束时间：{{ activity.endTime ? activity.endTime.replace('T', ' ') : '' }}
                                    </p>
                                </el-card>
                            </el-timeline-item>
                        </el-timeline>
                        <el-empty v-else description="暂无活动记录" />
                    </el-tab-pane>
                    <el-tab-pane label="🖼️ 社团相册">
                        <el-empty description="暂无照片" />
                    </el-tab-pane>
                    <el-tab-pane label="📥 成员审批" v-if="isPresident">
                        <el-table :data="pendingList" style="width: 100%" v-loading="auditLoading">
                            <el-table-column prop="userId" label="申请人ID" width="100" align="center" />
                            <el-table-column prop="name" label="申请人姓名" width="120" align="center" />
                            <el-table-column prop="create_time" label="申请时间" width="180" align="center">
                                <template #default="scope">
                                    {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ') : '尚未记录时间' }}
                                </template>
                            </el-table-column>

                            <el-table-column label="操作" width="180" align="center">
                                <template #default="scope">
                                    <el-button type="success" size="small"
                                        @click="handleAudit(scope.row.member_id, 1)">通过</el-button>
                                    <el-button type="danger" size="small"
                                        @click="handleAudit(scope.row.member_id, 2)">拒绝</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-tab-pane>
                    <el-tab-pane label="👥 成员列表">
                        <el-table :data="memberList" style="width: 100%" empty-text="社团还没有成员">
                            <el-table-column prop="userId" label="用户ID" width="120" align="center" />
                            <el-table-column prop="name" label="成员名称" width="120" align="center" />
                            <el-table-column label="社团角色" width="120" align="center">
                                <template #default="scope">
                                    <el-tag v-if="scope.row.roleType === 1" type="danger" effect="dark">社长</el-tag>
                                    <el-tag v-else-if="scope.row.roleType === 2" type="warning">管理员</el-tag>
                                    <el-tag v-else type="info">普通成员</el-tag>
                                </template>
                            </el-table-column>

                            <el-table-column prop="createTime" label="加入时间" align="center" />

                            <el-table-column label="操作" width="120" align="center" v-if="isPresident">
                                <template #default="scope">
                                    <el-button v-if="scope.row.roleType !== 1" type="danger" size="small" plain
                                        @click="handleKick(scope.row.memberId)">
                                        移出社团
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-tab-pane>
                </el-tabs>
            </el-col>
        </el-row>
    </div>
    <el-dialog v-model="showAddActivityDialog" title="发布新活动" width="500px">
        <el-form :model="activityForm" label-width="80px">
            <el-form-item label="活动标题">
                <el-input v-model="activityForm.title" placeholder="请输入活动标题" />
            </el-form-item>
            <el-form-item label="活动地点">
                <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
            </el-form-item>
            <el-form-item label="活动时间">
                <el-date-picker v-model="activityForm.timeRange" type="datetimerange" range-separator="至"
                    start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
            <el-form-item label="活动详情">
                <el-input type="textarea" :rows="4" v-model="activityForm.description" placeholder="请输入活动详情..." />
            </el-form-item>
            <el-form-item label="活动奖励">
                <el-input-number v-model="activityForm.pointsReward" :min="0" :max="100" label="积分奖励" />
                <span style="margin-left: 10px; color: gray;">积分 (可选)</span>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showAddActivityDialog = false">取消</el-button>
                <el-button type="primary" @click="submitActivity">发布</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import { InfoFilled, Calendar, User } from '@element-plus/icons-vue'
const user = JSON.parse(localStorage.getItem('user') || '{}')
const currentUserId = user.id || user.userId
const route = useRoute()
const router = useRouter()
const pendingList = ref([])
const auditLoading = ref(false)
const club = ref({})
const loading = ref(false)
const memberList = ref([])

const isPresident = computed(() => {
    return club.value.leaderId == currentUserId
})

const loadClubDetail = async () => {
    loading.value = true
    const clubId = route.params.id
    console.log("准备请求的ID是:", clubId)
    try {
        const res = await request.get(`/club/${clubId}`)
        if (res.code == 200) {
            club.value = res.data
            console.log("当前登录的用户ID是:", currentUserId, "类型:", typeof currentUserId)
            console.log("后端返回的社团信息是:", club.value)
        }
    } finally {
        loading.value = false
    }
}

const goBack = () => {
    router.back()
}

onMounted(async () => {
    await loadClubDetail()
    loadPendingList()
    loadMemberList()
    fetchActivities()
})

const handleQuitClub = () => {
    ElMessageBox.confirm('确定要退出该社团吗？', '退出确认', {
        type: 'warning',
    }).then(async () => {
        const res = await request.delete('/club/member/quit', {
            params: { clubId: route.params.id, userId: currentUserId }
        })
        if (res.code === 200) {
            ElMessage.success('已退出社团')
            router.back()
        } else {
            ElMessage.error(res.msg || '退出失败')
        }
    }).catch(() => { })
}

const handleDisbandClub = () => {
    ElMessageBox.confirm('确定要解散该社团吗？所有成员将被移出，此操作不可恢复！', '高危操作确认', {
        confirmButtonText: '确定解散',
        cancelButtonText: '取消',
        type: 'error',
    }).then(async () => {
        const res = await request.delete(`/club/delete/${route.params.id}`)
        if (res.code === 200) {
            ElMessage.success('社团已成功解散')
            router.back()
        } else {
            ElMessage.error(res.msg || '解散失败')
        }
    }).catch(() => { })
}

// 加载待审批列表
const loadPendingList = async () => {
    if (!isPresident.value) return

    auditLoading.value = true
    try {
        // 请求该社团下状态为 0 (待审核) 的记录
        const res = await request.get('/club/pending', {
            params: { clubId: route.params.id }
        })
        if (res.code === 200) {
            pendingList.value = res.data
        }
    } finally {
        auditLoading.value = false
    }
}

const handleAudit = (memberId, status) => {
    const actionText = status === 1 ? '通过' : '拒绝'
    ElMessageBox.confirm(`确定要 ${actionText} 该用户的申请吗？`, '审批确认', {
        type: status === 1 ? 'success' : 'warning'
    }).then(async () => {
        const res = await request.post('/club/audit', {
            id: memberId,
            status: status
        })

        if (res.code === 200) {
            ElMessage.success(`已${actionText}`)
            loadPendingList() // 操作成功后刷新列表
        } else {
            ElMessage.error(res.msg || '审批失败')
        }
    }).catch(() => { })
}

const loadMemberList = async () => {
    try {
        const res = await request.get('/club/members', {
            params: { clubId: route.params.id }
        })
        if (res.code === 200) {
            memberList.value = res.data
        }
    } catch (error) {
        console.error("加载成员列表失败:", error)
    }
}

const handleKick = (memberId) => {
    ElMessageBox.confirm('确定要将该成员移出社团吗？', '踢人确认', {
        confirmButtonText: '狠心移出',
        cancelButtonText: '取消',
        type: 'error',
    }).then(async () => {
        const res = await request.delete(`/club/kick/${memberId}`)
        if (res.code === 200) {
            ElMessage.success('已移出该成员')
            loadMemberList() // 操作成功后刷新成员列表
        } else {
            ElMessage.error(res.msg || '操作失败')
        }
    }).catch(() => { })
}

const activityList = ref([])
const showAddActivityDialog = ref(false)
const activityForm = ref({
    title: '',
    location: '',
    description: '',
    timeRange: [],
    pointsReward: 0
})

const fetchActivities = () => {
    const currentClubId = Number(route.query.id) || Number(route.query.clubId) || Number(route.params.id);
    if (!currentClubId) return;
    request.get(`/activity/club?clubId=${currentClubId}`).then(res => {
        if (res.code === 200) {
            activityList.value = res.data;
        } else {
            ElMessage.error(res.msg || '获取活动列表失败');
        }
    })
}

const submitActivity = () => {
    if (!activityForm.value.title || !activityForm.value.timeRange || activityForm.value.timeRange.length === 0) {
        ElMessage.warning('请填写完整的标题和时间')
        return
    }
    const currentClubId = Number(route.query.id) || Number(route.query.clubId) || Number(route.params.id);
    if (!currentClubId) {
        ElMessage.error('无法获取当前社团ID，请刷新页面重试');
        return;
    }
    const payload = {
        clubId: currentClubId,
        title: activityForm.value.title,
        location: activityForm.value.location,
        description: activityForm.value.description,
        startTime: activityForm.value.timeRange[0],
        endTime: activityForm.value.timeRange[1],
        pointsReward: activityForm.value.pointsReward || 0,
        status: 0
    }
    request.post('/activity/add', payload).then(res => {
        if (res.code === 200) {
            ElMessage.success('活动发布成功！')
            showAddActivityDialog.value = false
            activityForm.value = { title: '', location: '', description: '', timeRange: [], pointsReward: 0 }
            fetchActivities()
        } else {
            ElMessage.error(res.msg || '发布失败')
        }
    })
}
</script>

<style scoped>
.header-box {
    background: #fff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-title {
    font-weight: bold;
    font-size: 18px;
}

.info-card {
    border-radius: 12px;
}

.club-logo {
    text-align: center;
    padding: 20px 0;
}

.text-center {
    text-align: center;
    margin-top: 10px;
    color: #303133;
}

.detail-item {
    margin-bottom: 20px;
    font-size: 14px;
}

.label {
    font-weight: bold;
    margin-left: 8px;
    color: #606266;
}

.content {
    margin-top: 8px;
    color: #909399;
    line-height: 1.6;
}
</style>