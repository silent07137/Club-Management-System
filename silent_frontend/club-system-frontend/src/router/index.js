import { createRouter, createWebHistory } from 'vue-router'

// 1. 定义路由规则
const routes = [
  {
    path: '/',
    redirect: '/login' // 访问根路径时，自动重定向到登录页
  },
  {
    path: '/login',
    name: 'Login',
    // 这里的路径要确保和你的文件名、文件夹名完全一致
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/Index.vue'), // 这是后台的大骨架
    children: [
      {
        path: 'home',
        name: 'Home',
        // 首页暂时用一个简单的内联组件展示，后期可以新建一个 Home.vue
        component: () => import('../views/Home.vue')
      },
      {
        path: 'activity',
        name: 'Activity',
        // 🚩 这是我们刚才新建的社团活动管理页面
        component: () => import('../views/ActivityManage.vue')
      },
      {
        path: 'user-manage',
        name: 'UserManage',
        component: () => import('../views/UserManage.vue')
      },
      {
        path: 'clubs',
        name: 'ClubList',
        component: () => import('../views/ClubList.vue')
      },
      {
        path: 'audit',
        name: 'AuditManage',
        component: () => import('../views/AuditManage.vue')
      },
      {
        path: 'my-clubs',
        name: 'MyClubs',
        component: () => import('../views/MyClubs.vue')
      },
      {
        path: 'club-audit', // 🚩 管理员审批新社团的页面
        name: 'ClubAudit',
        component: () => import('../views/ClubAudit.vue')
      },
      {
        path: 'apply-club',
        name: 'ApplyClub',
        component: () => import('../views/ApplyClub.vue')
      },
      {
        path: 'club-detail/:id', // :id 是一个占位符，用来接收社团ID
        name: 'ClubDetail',
        component: () => import('../views/ClubDetail.vue')
      }
    ]
  }
]

// 2. 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router