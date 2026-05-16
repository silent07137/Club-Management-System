<template>
  <div class="page-shell home-page">
    <el-card class="page-card hero-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><HomeFilled /></el-icon>
            <div>
              <h2>欢迎回来，社团管理工作台</h2>
              <p>这里可以快速查看系统关键数据，并直达最常用的页面。</p>
            </div>
            </div>
            <div class="header-actions">
              <el-button :loading="loading" @click="loadStats">
                <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
                刷新数据
              </el-button>
            </div>
          </div>
        </template>

      <div class="hero-content">
        <div class="hero-text">
          <div class="eyebrow">控制台概览</div>
          <p>系统数据会实时从后端读取。你也可以直接从下方入口进入常用功能页。</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card user-card" shadow="hover">
          <div class="stat-top">
            <div class="stat-left">
              <div class="stat-label">注册成员总数</div>
              <div class="stat-value">{{ statsData.userCount }} <span>人</span></div>
            </div>
            <div class="stat-icon stat-icon-user">
              <el-icon><UserFilled /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card activity-card" shadow="hover">
          <div class="stat-top">
            <div class="stat-left">
              <div class="stat-label">累计社团活动</div>
              <div class="stat-value">{{ statsData.activityCount }} <span>场</span></div>
            </div>
            <div class="stat-icon stat-icon-activity">
              <el-icon><Calendar /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="8">
        <el-card class="stat-card action-card" shadow="hover">
          <div class="stat-top stat-top-compact">
            <div>
              <div class="stat-label">常用入口</div>
              <div class="action-list">
                <el-button type="primary" plain @click="router.push('/clubs')">
                  <el-icon style="margin-right: 4px;"><Tickets /></el-icon>
                  社团广场
                </el-button>
                <el-button type="success" plain @click="router.push('/my-clubs')">
                  <el-icon style="margin-right: 4px;"><Operation /></el-icon>
                  我的社团
                </el-button>
                <el-button v-if="isAdmin" type="warning" plain @click="router.push('/activity')">
                  <el-icon style="margin-right: 4px;"><Calendar /></el-icon>
                  全局活动管理
                </el-button>
                <el-button v-if="isAdmin || isLeader" type="warning" plain @click="router.push('/audit')">
                  <el-icon style="margin-right: 4px;"><Stamp /></el-icon>
                  入社审批
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="page-card tip-card" shadow="never">
      <div class="tip-title">使用提示</div>
      <div class="tip-text">
        主页数据会从后端实时读取。你也可以直接从左侧菜单进入社团广场、我的社团或审批页面。
      </div>
      <div v-if="lastUpdated" class="tip-meta">
        最后更新：{{ lastUpdated }}
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Refresh, UserFilled, Calendar, Tickets, Operation, Stamp } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const lastUpdated = ref('')
const statsData = ref({
  userCount: 0,
  activityCount: 0
})

const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))
const isAdmin = computed(() => user.value.role === 'ROLE_ADMIN')
const isLeader = computed(() => user.value.role === 'leader')

const loadStats = async () => {
  loading.value = true
  try {
    const res = await request.get('/stats/info')
    if (res.code === 200) {
      statsData.value = res.data
      lastUpdated.value = new Date().toLocaleString('zh-CN')
    } else {
      ElMessage.error(res.message || '获取统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.page-shell {
  padding: 20px;
}

.page-card {
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
}

.hero-card {
  margin-bottom: 20px;
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
  background: linear-gradient(135deg, #409eff, #79bbff);
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
}

.hero-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.hero-text {
  max-width: 720px;
}

.eyebrow {
  color: #909399;
  font-size: 13px;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.hero-text p {
  color: #606266;
  line-height: 1.7;
}

.stat-row {
  margin-top: 4px;
}

.stat-card {
  border-radius: 18px;
  min-height: 150px;
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
}

.stat-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.stat-top-compact {
  align-items: flex-start;
}

.stat-left {
  min-width: 0;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
  font-weight: 600;
}

.stat-value {
  font-size: 34px;
  font-weight: 700;
  line-height: 1;
  color: #303133;
}

.stat-value span {
  font-size: 15px;
  color: #909399;
  margin-left: 4px;
}

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  flex: 0 0 auto;
}

.stat-icon-user {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.stat-icon-activity {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.user-card .stat-value {
  color: #409eff;
}

.activity-card .stat-value {
  color: #67c23a;
}

.action-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tip-card {
  margin-top: 20px;
}

.tip-title {
  font-weight: 700;
  margin-bottom: 6px;
}

.tip-text {
  color: #606266;
  line-height: 1.6;
}

.tip-meta {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}

@media (max-width: 768px) {
  .page-header,
  .hero-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-list {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
