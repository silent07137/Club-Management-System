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
        <el-menu-item index="/user-manage">
          <el-icon>
            <User />
          </el-icon>
          <span>成员管理</span>
        </el-menu-item>
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
      </el-menu>
    </el-aside>

    <el-container>
      <el-header
        style="border-bottom: 1px solid #ddd; display: flex; align-items: center; justify-content: space-between;">
        <span>智慧校园 / 首页</span>
        <el-dropdown>
          <span class="el-dropdown-link" style="cursor: pointer;">
            管理员 <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
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
import { Menu, Bell, HomeFilled, User, Calendar, ArrowDown } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const handleLogout = () => {
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