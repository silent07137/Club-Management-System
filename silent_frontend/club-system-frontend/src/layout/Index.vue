<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu default-active="1" class="el-menu-vertical" background-color="#304156" text-color="#fff" router>
        <h3 style="color: white; text-align: center; padding: 20px 0;">社团管理系统</h3>
        <el-menu-item index="/home">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <span>首页看板</span>
        </el-menu-item>
        <el-menu-item index="/my-clubs">
          <el-icon>
            <User />
          </el-icon>
          <span>我的社团</span>
        </el-menu-item>
        <template v-if="isAdmin">
          <el-menu-item index="/user-manage">成员账号管理</el-menu-item>
          <el-menu-item index="/club-audit">社团开办审批</el-menu-item>
        </template>
        <el-menu-item index="/activity">
          <el-icon>
            <Calendar />
          </el-icon>
          <span>社团活动</span>
        </el-menu-item>
        <el-menu-item index="/clubs">
          <el-icon>
            <Menu />
          </el-icon>
          <span>社团广场</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin || isLeader" index="/audit">
          入社成员审批
        </el-menu-item>
        <el-menu-item v-if="!isAdmin && !isLeader" index="/apply-club">
          申请创建社团
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header
        style="border-bottom: 1px solid #ddd; display: flex; align-items: center; justify-content: space-between;">
        <span>智慧校园 / 首页</span>
        <el-dropdown>
          <span class="el-dropdown-link" style="cursor: pointer;">
            {{ displayName }} <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>{{ roleLabel }}</el-dropdown-item>
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
import { Menu, Checked, Bell, HomeFilled, User, Calendar, ArrowDown } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { computed, ref } from 'vue'
const userState = ref(JSON.parse(localStorage.getItem('user') || '{}'))

// 权限判定逻辑
const isAdmin = computed(() => userState.value.role === 'ROLE_ADMIN')
const isLeader = computed(() => userState.value.role === 'leader')
const displayName = computed(() => userState.value.name || '用户')
const roleLabel = computed(() => {
  if (userState.value.role === 'ROLE_ADMIN') return '当前身份：管理员'
  if (userState.value.role === 'leader') return '当前身份：社长'
  return '当前身份：普通成员'
})
const router = useRouter()
const handleLogout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  userState.value = {}
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-menu-vertical {
  height: 100%;
}
</style>
