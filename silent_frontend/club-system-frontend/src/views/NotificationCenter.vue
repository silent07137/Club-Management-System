<template>
  <div class="page-shell notification-center">
    <el-card class="page-card header-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><Bell /></el-icon>
            <div>
              <h2>消息通知中心</h2>
              <p>统一查看审批结果、活动提醒和系统消息。</p>
            </div>
          </div>
          <div class="header-actions">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" type="danger">
              <el-button :loading="loading" @click="loadNotifications">
                <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
                刷新
              </el-button>
            </el-badge>
            <el-button type="primary" plain :disabled="unreadCount === 0" :loading="markAllLoading" @click="handleMarkAllRead">
              全部已读
            </el-button>
          </div>
        </div>
      </template>

      <div class="toolbar">
        <el-radio-group v-model="filterMode" size="large">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="read">已读</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <el-skeleton :loading="loading" animated :count="3">
      <template #template>
        <el-card class="notification-card skeleton-card" shadow="never" v-for="i in 3" :key="i">
          <el-skeleton :rows="3" animated />
        </el-card>
      </template>

      <div v-if="filteredNotifications.length > 0">
        <el-card
          class="notification-card"
          shadow="hover"
          v-for="item in filteredNotifications"
          :key="item.notifyId"
          :class="{ unread: Number(item.isRead) === 0 }"
        >
          <div class="notification-header">
            <div class="notification-meta">
              <el-tag :type="getTypeTag(item.type)" size="small" effect="plain">
                {{ getTypeLabel(item.type) }}
              </el-tag>
              <el-tag :type="Number(item.isRead) === 0 ? 'danger' : 'success'" size="small">
                {{ Number(item.isRead) === 0 ? '未读' : '已读' }}
              </el-tag>
            </div>
            <div class="notification-time">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(item.createTime) }}</span>
            </div>
          </div>

          <div class="notification-content">
            {{ item.content || '暂无内容' }}
          </div>

          <div class="notification-footer">
            <el-button
              v-if="Number(item.isRead) === 0"
              type="primary"
              plain
              size="small"
              :loading="markingId === item.notifyId"
              @click="handleMarkRead(item.notifyId)"
            >
              标记已读
            </el-button>
          </div>
        </el-card>
      </div>

      <el-empty v-else description="暂无通知消息" />
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Bell, Clock, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const markAllLoading = ref(false)
const markingId = ref(null)
const filterMode = ref('all')
const notifications = ref([])
const unreadCount = ref(0)

const filteredNotifications = computed(() => {
  if (filterMode.value === 'unread') {
    return notifications.value.filter((item) => Number(item.isRead) === 0)
  }
  if (filterMode.value === 'read') {
    return notifications.value.filter((item) => Number(item.isRead) === 1)
  }
  return notifications.value
})

const getTypeLabel = (type) => {
  if (Number(type) === 1) return '系统消息'
  if (Number(type) === 2) return '活动提醒'
  if (Number(type) === 3) return '积分变动'
  return '通知'
}

const getTypeTag = (type) => {
  if (Number(type) === 1) return 'info'
  if (Number(type) === 2) return 'warning'
  if (Number(type) === 3) return 'success'
  return 'default'
}

const formatTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

const loadNotifications = async () => {
  loading.value = true
  try {
    const [listRes, countRes] = await Promise.all([
      request.get('/notification/my'),
      request.get('/notification/unread-count')
    ])
    notifications.value = listRes.data || []
    unreadCount.value = Number(countRes.data?.count || 0)
  } catch (error) {
    ElMessage.error('获取通知失败')
  } finally {
    loading.value = false
  }
}

const handleMarkRead = async (notifyId) => {
  markingId.value = notifyId
  try {
    const res = await request.post(`/notification/read/${notifyId}`)
    if (res.code === 200) {
      ElMessage.success(res.message || '已标记为已读')
      await loadNotifications()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } finally {
    markingId.value = null
  }
}

const handleMarkAllRead = async () => {
  markAllLoading.value = true
  try {
    const res = await request.post('/notification/read-all')
    if (res.code === 200) {
      ElMessage.success(res.message || '已全部标记为已读')
      await loadNotifications()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } finally {
    markAllLoading.value = false
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.page-shell {
  padding: 20px;
  background: #f4f7fb;
  min-height: 100vh;
}

.header-card {
  margin-bottom: 20px;
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
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
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
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
  gap: 10px;
  flex-wrap: wrap;
}

.toolbar {
  display: flex;
  justify-content: flex-start;
}

.notification-card {
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
  margin-bottom: 16px;
}

.notification-card.unread {
  border: 1px solid rgba(245, 108, 108, 0.18);
  background: linear-gradient(180deg, #fffdfd 0%, #ffffff 100%);
}

.notification-card :deep(.el-card__body) {
  padding: 18px 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.notification-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.notification-time {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.notification-content {
  margin: 14px 0 0;
  color: #303133;
  line-height: 1.7;
  font-size: 14px;
}

.notification-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.skeleton-card {
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-shell {
    padding: 12px;
  }
}
</style>
