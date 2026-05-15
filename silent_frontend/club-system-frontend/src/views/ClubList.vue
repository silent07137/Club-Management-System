<template>
  <div class="club-square">
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div>
          <h2>社团广场</h2>
          <p>浏览所有已开放社团，进入详情查看后再决定是否申请加入。</p>
        </div>
        <div class="toolbar-actions">
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
          <el-button :loading="loading" @click="loadClubs">刷新</el-button>
        </div>
      </div>
    </el-card>

    <el-skeleton :loading="loading" animated :count="3">
      <template #template>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="i in 3" :key="i">
            <el-skeleton-item variant="rect" style="height: 220px; margin-bottom: 20px" />
          </el-col>
        </el-row>
      </template>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="club in filteredClubList" :key="club.clubId">
          <el-card class="club-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div>
                  <div class="club-name">{{ club.name }}</div>
                  <div class="club-id">编号：{{ club.clubId }}</div>
                </div>
                <el-tag type="success" size="small">开放中</el-tag>
              </div>
            </template>

            <div class="club-desc">
              {{ club.description || '这个社团还没有填写简介。' }}
            </div>

            <div class="card-footer">
              <el-button text @click="goDetail(club)">查看详情</el-button>
              <el-button
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
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const applyingClubId = ref(null)
const keyword = ref('')
const clubList = ref([])

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
})
</script>

<style scoped>
.club-square {
  padding: 20px;
}

.toolbar-card {
  margin-bottom: 20px;
  border-radius: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: center;
}

.toolbar h2 {
  margin: 0 0 8px;
  color: #1f2d3d;
}

.toolbar p {
  margin: 0;
  color: #606266;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 280px;
}

.club-card {
  margin-bottom: 20px;
  transition: 0.3s;
  border-radius: 14px;
}

.club-card:hover {
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.club-name {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.club-id {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.club-desc {
  min-height: 64px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.card-footer {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
