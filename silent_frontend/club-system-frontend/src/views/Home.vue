<template>
  <div class="home-page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-content">
        <div>
          <div class="eyebrow">控制台概览</div>
          <h2>欢迎回来，社团管理工作台</h2>
          <p>这里可以快速查看系统关键数据，并直达最常用的页面。</p>
        </div>
        <div class="hero-actions">
          <el-button :loading="loading" @click="loadStats">刷新数据</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card user-card" shadow="hover">
          <div class="stat-top">
            <div>
              <div class="stat-label">注册成员总数</div>
              <div class="stat-value">{{ statsData.userCount }} <span>人</span></div>
            </div>
            <div class="stat-icon">👥</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card activity-card" shadow="hover">
          <div class="stat-top">
            <div>
              <div class="stat-label">累计社团活动</div>
              <div class="stat-value">{{ statsData.activityCount }} <span>场</span></div>
            </div>
            <div class="stat-icon">🎉</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="8">
        <el-card class="stat-card action-card" shadow="hover">
          <div class="stat-top">
            <div>
              <div class="stat-label">常用入口</div>
              <div class="action-list">
                <el-button type="primary" plain @click="router.push('/clubs')">社团广场</el-button>
                <el-button type="success" plain @click="router.push('/my-clubs')">我的社团</el-button>
                <el-button v-if="isAdmin || isLeader" type="warning" plain @click="router.push('/audit')">入社审批</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="tip-card" shadow="never">
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
.home-page {
  padding: 20px;
}

.hero-card,
.tip-card {
  border-radius: 14px;
}

.hero-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.eyebrow {
  color: #909399;
  font-size: 13px;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.hero-content h2 {
  margin: 0 0 10px;
  font-size: 28px;
  color: #1f2d3d;
}

.hero-content p {
  margin: 0;
  color: #606266;
}

.stat-row {
  margin-top: 20px;
}

.stat-card {
  border-radius: 14px;
  min-height: 140px;
}

.stat-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
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
  font-size: 36px;
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
  .hero-content {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
