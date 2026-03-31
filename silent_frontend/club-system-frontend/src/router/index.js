import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/Index.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: 'activity',
        name: 'Activity',
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
        path: 'club-audit',
        name: 'ClubAudit',
        component: () => import('../views/ClubAudit.vue')
      },
      {
        path: 'apply-club',
        name: 'ApplyClub',
        component: () => import('../views/ApplyClub.vue')
      },
      {
        path: 'club-detail/:id',
        name: 'ClubDetail',
        component: () => import('../views/ClubDetail.vue')
      }
    ]
  }
]


const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router