<template>
  <div class="club-detail-page" v-loading="pageLoading">
    <el-card class="header-card" shadow="never">
      <el-page-header @back="goBack">
        <template #content>
          <div class="header-content">
            <span class="header-title">{{ club.name || '社团详情' }}</span>
            <el-tag :type="clubStatusTag" size="small">{{ clubStatusLabel }}</el-tag>
          </div>
        </template>
        <template #extra>
          <div class="header-actions">
            <el-button @click="refreshAll">刷新</el-button>
            <el-button v-if="isPresident" type="danger" @click="handleDisbandClub">解散社团</el-button>
            <el-button v-else-if="isJoined" type="warning" plain @click="handleQuitClub">退出社团</el-button>
          </div>
        </template>
      </el-page-header>
    </el-card>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :md="8">
        <el-card class="info-card" shadow="hover">
          <div class="club-logo">
            <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          </div>

          <h2 class="text-center">{{ club.name || '未命名社团' }}</h2>
          <p class="sub-title">{{ club.description || '当前社团还没有填写简介。' }}</p>

          <el-divider />

          <div class="detail-item">
            <el-icon><InfoFilled /></el-icon>
            <span class="label">社团状态</span>
            <el-tag :type="clubStatusTag" size="small">{{ clubStatusLabel }}</el-tag>
          </div>

          <div class="detail-item">
            <el-icon><Calendar /></el-icon>
            <span class="label">成立时间</span>
            <span class="content">{{ formatTime(club.createTime) }}</span>
          </div>

          <div class="detail-item">
            <el-icon><User /></el-icon>
            <span class="label">当前身份</span>
            <el-tag :type="currentRoleTag" size="small">{{ currentRoleLabel }}</el-tag>
          </div>

          <div v-if="club.rejectReason" class="reject-box">
            <div class="reject-title">驳回理由</div>
            <div class="reject-content">{{ club.rejectReason }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="16">
        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="社团活动" name="activity">
            <div class="tab-toolbar" v-if="isPresident">
              <el-button type="primary" @click="showAddActivityDialog = true">+ 发布活动</el-button>
            </div>

            <el-skeleton :loading="activityLoading" animated :rows="4">
              <template #default>
                <el-timeline v-if="activityList.length > 0">
                  <el-timeline-item
                    v-for="activity in activityList"
                    :key="activity.activityId"
                    :timestamp="formatTime(activity.startTime)"
                    placement="top"
                    type="primary"
                  >
                    <el-card>
                      <h3 style="margin-top: 0">{{ activity.title }}</h3>
                      <p><strong>地点：</strong>{{ activity.location || '未填写' }}</p>
                      <p><strong>详情：</strong>{{ activity.description || '暂无详情' }}</p>
                      <p class="activity-meta">
                        结束时间：{{ formatTime(activity.endTime) }}
                      </p>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
                <el-empty v-else description="当前没有活动记录" />
              </template>
            </el-skeleton>
          </el-tab-pane>

          <el-tab-pane v-if="isPresident" label="成员审批" name="audit">
            <el-table :data="pendingList" style="width: 100%" v-loading="pendingLoading" empty-text="当前没有待审批申请">
              <el-table-column prop="userId" label="申请人ID" width="120" align="center" />
              <el-table-column prop="name" label="申请人姓名" width="140" align="center" />
              <el-table-column prop="createTime" label="申请时间" min-width="180" align="center">
                <template #default="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template #default="scope">
                  <el-button type="success" size="small" @click="handleAudit(scope.row.memberId, 1)">通过</el-button>
                  <el-button type="danger" size="small" @click="handleAudit(scope.row.memberId, 2)">拒绝</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="成员列表" name="members">
            <el-table :data="memberList" style="width: 100%" v-loading="memberLoading" empty-text="社团还没有成员">
              <el-table-column prop="userId" label="用户ID" width="120" align="center" />
              <el-table-column prop="name" label="成员姓名" width="140" align="center" />
              <el-table-column label="社团角色" width="140" align="center">
                <template #default="scope">
                  <el-tag v-if="scope.row.roleType === 1" type="danger" effect="dark">社长</el-tag>
                  <el-tag v-else-if="scope.row.roleType === 2" type="warning">管理员</el-tag>
                  <el-tag v-else type="info">普通成员</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="加入时间" min-width="180" align="center">
                <template #default="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column v-if="isPresident" label="操作" width="130" align="center">
                <template #default="scope">
                  <el-button
                    v-if="scope.row.roleType !== 1"
                    type="danger"
                    size="small"
                    plain
                    @click="handleKick(scope.row.memberId)"
                  >
                    移出
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <el-dialog v-model="showAddActivityDialog" title="发布新活动" width="520px">
      <el-form :model="activityForm" label-width="90px">
        <el-form-item label="活动标题">
          <el-input v-model="activityForm.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="activityForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动详情">
          <el-input
            v-model="activityForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动详情"
          />
        </el-form-item>
        <el-form-item label="积分奖励">
          <el-input-number v-model="activityForm.pointsReward" :min="0" :max="100" />
          <span class="reward-tip">分</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddActivityDialog = false">取消</el-button>
        <el-button type="primary" :loading="submittingActivity" @click="submitActivity">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, InfoFilled, User } from '@element-plus/icons-vue'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const currentUserId = Number(user.id || user.userId)

const club = ref({})
const pendingList = ref([])
const memberList = ref([])
const activityList = ref([])
const activeTab = ref('activity')
const pageLoading = ref(false)
const pendingLoading = ref(false)
const memberLoading = ref(false)
const activityLoading = ref(false)
const showAddActivityDialog = ref(false)
const submittingActivity = ref(false)
const pendingActionLoading = ref(false)
const activityForm = ref({
  title: '',
  location: '',
  description: '',
  timeRange: [],
  pointsReward: 0
})

const clubId = computed(() => Number(route.query.id) || Number(route.query.clubId) || Number(route.params.id))
const isJoined = computed(() => !!currentMember.value)
const isPresident = computed(() => Number(club.value.leaderId) === currentUserId)
const currentMember = computed(() => memberList.value.find((item) => Number(item.userId) === currentUserId) || null)
const clubStatusLabel = computed(() => {
  if (club.value.status === 1) return '正常'
  if (club.value.status === 2) return '已驳回'
  return '审核中'
})
const clubStatusTag = computed(() => {
  if (club.value.status === 1) return 'success'
  if (club.value.status === 2) return 'danger'
  return 'warning'
})
const currentRoleLabel = computed(() => {
  if (isPresident.value) return '社长'
  if (!isJoined.value) return '未加入'
  if (currentMember.value?.roleType === 2) return '管理员'
  return '普通成员'
})
const currentRoleTag = computed(() => {
  if (isPresident.value) return 'danger'
  if (!isJoined.value) return 'info'
  if (currentMember.value?.roleType === 2) return 'warning'
  return 'success'
})

const formatTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

const goBack = () => {
  router.back()
}

const loadClubDetail = async () => {
  if (!clubId.value) {
    ElMessage.error('无法获取社团ID')
    return
  }

  const res = await request.get(`/club/${clubId.value}`)
  if (res.code === 200) {
    club.value = res.data || {}
  }
}

const loadPendingList = async () => {
  if (!isPresident.value || !clubId.value) {
    pendingList.value = []
    return
  }

  pendingLoading.value = true
  try {
    const res = await request.get('/club/pending', {
      params: { clubId: clubId.value }
    })
    if (res.code === 200) {
      pendingList.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取待审批列表失败')
    }
  } finally {
    pendingLoading.value = false
  }
}

const loadMemberList = async () => {
  if (!clubId.value) return

  memberLoading.value = true
  try {
    const res = await request.get('/club/members', {
      params: { clubId: clubId.value }
    })
    if (res.code === 200) {
      memberList.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取成员列表失败')
    }
  } finally {
    memberLoading.value = false
  }
}

const fetchActivities = async () => {
  if (!clubId.value) return

  activityLoading.value = true
  try {
    const res = await request.get('/activity/club', {
      params: { clubId: clubId.value }
    })
    if (res.code === 200) {
      activityList.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取活动列表失败')
    }
  } finally {
    activityLoading.value = false
  }
}

const refreshAll = async () => {
  pageLoading.value = true
  try {
    await loadClubDetail()
    await Promise.all([loadPendingList(), loadMemberList(), fetchActivities()])
  } finally {
    pageLoading.value = false
  }
}

const handleQuitClub = () => {
  ElMessageBox.confirm('确定要退出该社团吗？', '退出确认', { type: 'warning' })
    .then(async () => {
      const res = await request.delete('/club/member/quit', {
        params: { clubId: clubId.value, userId: currentUserId }
      })
      if (res.code === 200) {
        ElMessage.success(res.message || '已退出社团')
        router.push('/my-clubs')
      } else {
        ElMessage.error(res.message || '退出失败')
      }
    })
    .catch(() => {})
}

const handleDisbandClub = () => {
  ElMessageBox.confirm('确定要解散该社团吗？该操作不可恢复。', '高危操作确认', {
    confirmButtonText: '确定解散',
    cancelButtonText: '取消',
    type: 'error'
  })
    .then(async () => {
      const res = await request.delete(`/club/delete/${clubId.value}`)
      if (res.code === 200) {
        ElMessage.success(res.message || '社团已解散')
        router.push('/clubs')
      } else {
        ElMessage.error(res.message || '解散失败')
      }
    })
    .catch(() => {})
}

const handleAudit = async (memberId, status) => {
  const actionText = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${actionText}该成员的申请吗？`, '审批确认', {
      type: status === 1 ? 'success' : 'warning'
    })
    pendingActionLoading.value = true
    const res = await request.post('/club/audit', {
      id: memberId,
      status
    })
    if (res.code === 200) {
      ElMessage.success(res.message || '审批完成')
      await Promise.all([loadPendingList(), loadMemberList()])
    } else {
      ElMessage.error(res.message || '审批失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    pendingActionLoading.value = false
  }
}

const handleKick = (memberId) => {
  ElMessageBox.confirm('确定要将该成员移出社团吗？', '移出确认', {
    confirmButtonText: '移出',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      const res = await request.delete(`/club/kick/${memberId}`)
      if (res.code === 200) {
        ElMessage.success(res.message || '成员已移出')
        await loadMemberList()
      } else {
        ElMessage.error(res.message || '移出失败')
      }
    })
    .catch(() => {})
}

const submitActivity = async () => {
  if (!activityForm.value.title || !activityForm.value.timeRange || activityForm.value.timeRange.length === 0) {
    ElMessage.warning('请填写完整的活动标题和时间')
    return
  }

  const payload = {
    clubId: clubId.value,
    title: activityForm.value.title,
    location: activityForm.value.location,
    description: activityForm.value.description,
    startTime: activityForm.value.timeRange[0],
    endTime: activityForm.value.timeRange[1],
    pointsReward: activityForm.value.pointsReward || 0,
    status: 0
  }

  submittingActivity.value = true
  try {
    const res = await request.post('/activity/add', payload)
    if (res.code === 200) {
      ElMessage.success(res.message || '活动已发布')
      showAddActivityDialog.value = false
      activityForm.value = { title: '', location: '', description: '', timeRange: [], pointsReward: 0 }
      await fetchActivities()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } finally {
    submittingActivity.value = false
  }
}

onMounted(() => {
  refreshAll()
})
</script>

<style scoped>
.club-detail-page {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
  border-radius: 14px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  font-weight: 700;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.content-row {
  margin-top: 4px;
}

.info-card {
  border-radius: 14px;
  min-height: 100%;
}

.club-logo {
  text-align: center;
  padding: 12px 0 20px;
}

.text-center {
  text-align: center;
  margin: 0;
  color: #303133;
}

.sub-title {
  margin: 8px 0 0;
  color: #606266;
  line-height: 1.6;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}

.label {
  font-weight: 700;
  min-width: 72px;
}

.content {
  color: #303133;
}

.reject-box {
  margin-top: 16px;
  padding: 12px;
  border-radius: 10px;
  background: #fef0f0;
  color: #c45656;
}

.reject-title {
  font-weight: 700;
  margin-bottom: 6px;
}

.reject-content {
  line-height: 1.6;
}

.tab-toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.activity-meta {
  color: #909399;
  font-size: 13px;
  margin-bottom: 0;
}

.reward-tip {
  margin-left: 10px;
  color: #909399;
}

@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
  }

  .header-actions {
    margin-top: 10px;
  }
}
</style>
