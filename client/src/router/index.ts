import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/views/HomePage.vue'),
    },
    {
      path: '/login',
      component: () => import('@/views/LoginPage.vue'),
    },
    {
      path: '/register',
      component: () => import('@/views/RegisterPage.vue'),
    },
    {
      path: '/admin/login',
      component: () => import('@/views/admin/LoginPage.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/DashboardPage.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('@/views/NotFoundPage.vue'),
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isLoggedIn()) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  if (to.meta.requiresAdmin && !auth.isAdmin()) {
    return next('/')
  }

  next()
})

export default router
