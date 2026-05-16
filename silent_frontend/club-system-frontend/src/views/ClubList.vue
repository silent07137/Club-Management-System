<template>
  <div class="page-shell club-square">
    <el-card class="page-card toolbar-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><OfficeBuilding /></el-icon>
            <div>
              <h2>社团广场</h2>
              <p>浏览所有已开放社团，进入详情查看后再决定是否申请加入。</p>
            </div>
          </div>
          <div class="header-actions">
            <el-badge :value="filteredClubList.length" :hidden="filteredClubList.length === 0" type="primary">
              <el-button :loading="loading" @click="loadClubs">
                <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
                刷新
              </el-button>
            </el-badge>
          </div>
        </div>
      </template>

      <div class="toolbar">
        <el-input
          v-model="keyword"
          clearable
          placeholder="搜索社团名称或简介"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" plain @click="loadClubs">重新加载</el-button>
      </div>
    </el-card>

    <el-skeleton :loading="loading" animated :count="3">
      <template #template>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="i in 3" :key="i">
            <el-skeleton-item variant="rect" class="club-skeleton" />
          </el-col>
        </el-row>
      </template>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="club in filteredClubList" :key="club.clubId">
          <el-card class="club-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="card-title">
                  <div class="club-name">{{ club.name }}</div>
                  <div class="club-meta">
                    <span><el-icon><Tickets /></el-icon> 编号：{{ club.clubId }}</span>
                  </div>
                </div>
                <div class="card-status">
                  <el-tag type="success" size="small">开放中</el-tag>
                  <el-tag :type="getMembershipTagType(club.clubId)" size="small" effect="plain">
                    {{ getMembershipLabel(club.clubId) }}
                  </el-tag>
                </div>
              </div>
            </template>

            <div class="club-desc">
              {{ club.description || '这个社团还没有填写简介。' }}
            </div>

            <div class="card-footer">
              <el-button text @click="goDetail(club)">
                <el-icon style="margin-right: 4px;"><Operation /></el-icon>
                查看详情
              </el-button>
              <el-button
                v-if="canApplyClub(club.clubId)"
                type="primary"
                :loading="applyingClubId === club.clubId"
                :disabled="applyingClubId !== null && applyingClubId !== club.clubId"
                @click="handleApply(club)"
              >
                申请加入
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-skeleton>

    <el-empty v-if="!loading && filteredClubList.length === 0" description="没有找到匹配的社团" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Refresh, OfficeBuilding, Tickets, Operation } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const applyingClubId = ref(null)
const keyword = ref('')
const clubList = ref([])
const clubMemberships = ref([])

const getMembershipRecord = (clubId) => {
  return clubMemberships.value.find((item) => Number(item.clubId) === Number(clubId)) || null
}

const getMembershipTagType = (clubId) => {
  const record = getMembershipRecord(clubId)
  if (!record) return 'info'
  if (Number(record.joinStatus) === 1) return 'success'
  if (Number(record.joinStatus) === 0) return 'warning'
  if (Number(record.joinStatus) === 2) return 'danger'
  return 'info'
}

const getMembershipLabel = (clubId) => {
  const record = getMembershipRecord(clubId)
  if (!record) return '可申请'
  if (Number(record.joinStatus) === 1) return '已加入'
  if (Number(record.joinStatus) === 0) return '申请中'
  if (Number(record.joinStatus) === 2) return '已拒绝'
  return '状态未知'
}

const canApplyClub = (clubId) => {
  const record = getMembershipRecord(clubId)
  return !record
}

const filteredClubList = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return clubList.value
  return clubList.value.filter((club) => {
    const name = String(club.name || '').toLowerCase()
    const desc = String(club.description || '').toLowerCase()
    return name.includes(kw) || desc.includes(kw)
  })
})

const loadClubs = async () => {
  loading.value = true
  try {
    const res = await request.get('/club/list')
    if (res.code === 200) {
      clubList.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取社团列表失败')
    }
  } catch (error) {
    ElMessage.error('获取社团列表失败')
  } finally {
    loading.value = false
  }
}

const loadMemberships = async () => {
  const userStore = JSON.parse(localStorage.getItem('user') || '{}')
  const finalUserId = userStore.userId || userStore.id
  if (!finalUserId) {
    clubMemberships.value = []
    return
  }

  try {
    const res = await request.get('/club/member/my-status', {
      params: { userId: finalUserId }
    })
    if (res.code === 200) {
      clubMemberships.value = res.data || []
    }
  } catch (error) {
    clubMemberships.value = []
  }
}

const goDetail = (club) => {
  router.push(`/club-detail/${club.clubId}`)
}

const handleApply = (club) => {
  const userStore = JSON.parse(localStorage.getItem('user') || '{}')
  const finalUserId = userStore.userId || userStore.id
  if (!finalUserId) {
    ElMessage.error('用户信息已失效，请重新登录')
    return
  }

  ElMessageBox.confirm(`确定申请加入「${club.name}」吗？`, '申请确认', { type: 'info' })
    .then(async () => {
      applyingClubId.value = club.clubId
      try {
        const res = await request.post('/club/member/apply', {
          userId: finalUserId,
          clubId: club.clubId
        })

        if (res.code === 200) {
          ElMessage.success(res.message || '申请成功')
        } else {
          ElMessage.warning(res.message || '申请失败')
        }
      } finally {
        applyingClubId.value = null
      }
    })
    .catch(() => {})
}

onMounted(() => {
  loadClubs()
  loadMemberships()
})
</script>

<style scoped>
.page-shell {
  padding: 20px;
}

.toolbar-card,
.club-card {
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
}

.toolbar-card {
  margin-bottom: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: center;
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
  font-size: 20px;
  line-height: 1.2;
}

.page-title p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}

.header-actions {
  display: flex;
  align-items: center;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 320px;
}

.club-card {
  margin-bottom: 20px;
  transition: 0.3s;
}

.club-card:hover {
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.card-status {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.card-status :deep(.el-tag),
.card-header :deep(.el-tag) {
  border-radius: 999px;
  height: 28px;
  line-height: 26px;
  padding: 0 12px;
}

.card-title {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.club-name {
  font-size: 17px;
  font-weight: 700;
  color: #303133;
  line-height: 1.25;
}

.club-meta {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.club-desc {
  min-height: 64px;
  color: #606266;
  font-size: 14px;
  line-height: 1.7;
}

.card-footer {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.club-skeleton {
  height: 220px;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .page-header,
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
