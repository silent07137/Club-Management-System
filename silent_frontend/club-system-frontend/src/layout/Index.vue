<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu :default-active="route.path" class="el-menu-vertical" background-color="#304156" text-color="#fff" router>
        <div class="sidebar-brand">
          <div class="brand-mark">SC</div>
          <div>
            <div class="brand-title">社团管理系统</div>
            <div class="brand-subtitle">Club Admin Console</div>
          </div>
        </div>
        <el-menu-item index="/home">
          <span class="menu-icon menu-icon-home">
            <el-icon><HomeFilled /></el-icon>
          </span>
          <span>首页看板</span>
        </el-menu-item>
        <el-menu-item index="/notifications">
          <span class="menu-icon menu-icon-notification">
            <el-icon><Bell /></el-icon>
          </span>
          <span>消息通知中心</span>
        </el-menu-item>
        <el-menu-item index="/my-clubs">
          <span class="menu-icon menu-icon-club">
            <el-icon><User /></el-icon>
          </span>
          <span>我的社团</span>
        </el-menu-item>
        <template v-if="isAdmin">
          <el-menu-item index="/user-manage">
            <span class="menu-icon menu-icon-admin">
              <el-icon><UserFilled /></el-icon>
            </span>
            <span>成员账号管理</span>
          </el-menu-item>
          <el-menu-item index="/club-audit">
            <span class="menu-icon menu-icon-audit">
              <el-icon><DocumentChecked /></el-icon>
            </span>
            <span>社团开办审批</span>
          </el-menu-item>
        </template>
        <el-menu-item v-if="isAdmin" index="/activity">
          <span class="menu-icon menu-icon-activity">
            <el-icon><Calendar /></el-icon>
          </span>
          <span>全局活动管理</span>
        </el-menu-item>
        <el-menu-item index="/clubs">
          <span class="menu-icon menu-icon-square">
            <el-icon><Menu /></el-icon>
          </span>
          <span>社团广场</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin || isLeader" index="/audit">
          <span class="menu-icon menu-icon-review">
            <el-icon><Stamp /></el-icon>
          </span>
          <span>入社成员审批</span>
        </el-menu-item>
        <el-menu-item v-if="!isAdmin && !isLeader" index="/apply-club">
          <span class="menu-icon menu-icon-create">
            <el-icon><CirclePlus /></el-icon>
          </span>
          <span>申请创建社团</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div class="topbar-left">
          <div class="topbar-title">{{ topbarTitle }}</div>
          <div class="topbar-subtitle">社团管理控制台</div>
        </div>
        <el-dropdown trigger="click" placement="bottom-end">
          <div class="user-trigger">
            <div class="user-avatar">
              {{ (displayName || 'U').slice(0, 1) }}
            </div>
            <div class="user-meta">
              <div class="user-name">{{ displayName }}</div>
              <div class="user-role">{{ roleLabel }}</div>
            </div>
            <el-icon class="user-arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-menu">
              <el-dropdown-item disabled>
                <span class="dropdown-role">{{ roleLabel }}</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main>
        <router-view /> </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { Menu, HomeFilled, User, UserFilled, Calendar, ArrowDown, DocumentChecked, Stamp, CirclePlus, Bell } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { computed, ref } from 'vue'
import request from '../utils/request'
const userState = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const route = useRoute()

// 权限判定逻辑
const isAdmin = computed(() => userState.value.role === 'ROLE_ADMIN')
const isLeader = computed(() => userState.value.role === 'leader')
const displayName = computed(() => userState.value.name || '用户')
const topbarTitle = computed(() => {
  if (route.path === '/home') return '智慧校园 / 首页看板'
  if (route.path === '/notifications') return '智慧校园 / 消息通知中心'
  if (route.path === '/clubs') return '智慧校园 / 社团广场'
  if (route.path === '/my-clubs') return '智慧校园 / 我的社团'
  return '智慧校园 / 社团管理控制台'
})
const roleLabel = computed(() => {
  if (userState.value.role === 'ROLE_ADMIN') return '当前身份：管理员'
  if (userState.value.role === 'leader') return '当前身份：社长'
  return '当前身份：普通成员'
})
const router = useRouter()
const handleLogout = () => {
  request.post('/user/logout').finally(() => {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    userState.value = {}
    router.push('/login')
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: #f4f7fb;
}

.topbar {
  border-bottom: 1px solid #e6e8eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 24px rgba(31, 45, 61, 0.04);
  border-radius: 0 0 18px 18px;
  margin: 12px 12px 0 12px;
  height: 68px;
}

.topbar-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.topbar-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2d3d;
}

.topbar-subtitle {
  font-size: 12px;
  color: #909399;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 14px;
  cursor: pointer;
  transition: 0.2s ease;
  background: #fff;
  border: 1px solid #e6e8eb;
  box-shadow: 0 4px 14px rgba(31, 45, 61, 0.06);
}

.user-trigger:hover {
  transform: translateY(-1px);
  border-color: #cfd7e3;
  box-shadow: 0 8px 18px rgba(31, 45, 61, 0.1);
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  background: linear-gradient(135deg, #409eff, #79bbff);
  flex: 0 0 auto;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 700;
  color: #1f2d3d;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #606266;
  line-height: 1.2;
}

.user-arrow {
  color: #909399;
}

.user-menu {
  min-width: 180px;
}

.dropdown-role {
  font-weight: 600;
  color: #1f2d3d;
}

.el-menu-vertical {
  height: 100%;
  border-right: none;
  padding-top: 6px;
  background: linear-gradient(180deg, #304156 0%, #263445 100%);
  box-shadow: 8px 0 24px rgba(31, 45, 61, 0.12);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 16px 14px;
  color: #fff;
}

.brand-mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #67c23a, #95d475);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 2px;
  font-size: 11px;
  opacity: 0.7;
}

.el-menu-vertical :deep(.el-menu-item) {
  margin: 5px 10px;
  border-radius: 14px;
  height: 46px;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.el-menu-vertical :deep(.el-menu-item.is-active) {
  background: rgba(103, 194, 58, 0.2);
}

.menu-icon {
  width: 28px;
  height: 28px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  background: rgba(255, 255, 255, 0.12);
  flex: 0 0 auto;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.06);
}

.menu-icon-home {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.menu-icon-club {
  background: linear-gradient(135deg, #909399, #b1b3b8);
}

.menu-icon-notification {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}

.menu-icon-admin {
  background: linear-gradient(135deg, #e6a23c, #eebe77);
}

.menu-icon-audit {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.menu-icon-activity {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.menu-icon-square {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.menu-icon-review {
  background: linear-gradient(135deg, #8e71c7, #b39ddb);
}

.menu-icon-create {
  background: linear-gradient(135deg, #13c2c2, #5cd9d5);
}

.el-main {
  padding: 0;
}

.el-container > .el-container {
  margin: 12px 12px 12px 0;
  border-radius: 18px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
}

@media (max-width: 768px) {
  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 16px 18px;
    margin: 12px 12px 0 12px;
    height: auto;
  }

  .user-trigger {
    width: 100%;
    justify-content: space-between;
  }

  .el-container > .el-container {
    margin: 12px;
  }
}
</style>
