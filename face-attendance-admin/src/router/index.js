import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/login/Login.vue'
import Layout from '@/layout/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import UserManage from '@/views/user/UserManage.vue'
import AttendanceManage from '@/views/attendance/AttendanceManage.vue'
import AppealManage from '@/views/appeal/AppealManage.vue'
import AntiCheat from '@/views/AntiCheat.vue'
import Statistics from '@/views/statistics/Statistics.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页' }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: UserManage,
        meta: { title: '用户管理' }
      },
      {
        path: 'attendance',
        name: 'AttendanceManage',
        component: AttendanceManage,
        meta: { title: '考勤管理' }
      },
      {
        path: 'appeal',
        name: 'AppealManage',
        component: AppealManage,
        meta: { title: '申诉管理' }
      },
      {
        path: 'anticheat',
        name: 'AntiCheat',
        component: AntiCheat,
        meta: { title: '防作弊记录' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: Statistics,
        meta: { title: '考勤统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
