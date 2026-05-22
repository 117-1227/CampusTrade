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
      path: '/product/:id',
      component: () => import('@/views/ProductDetailPage.vue'),
    },
    {
      path: '/publish',
      component: () => import('@/views/PublishPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      component: () => import('@/views/ProfilePage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/user/:id',
      component: () => import('@/views/SellerPage.vue'),
    },
    {
      path: '/chat',
      component: () => import('@/views/ChatPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/chat/:id',
      component: () => import('@/views/ChatPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/orders',
      component: () => import('@/views/OrdersPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin/login',
      component: () => import('@/views/admin/LoginPage.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', component: () => import('@/views/admin/DashboardPage.vue') },
        { path: 'audit', component: () => import('@/views/admin/AuditPage.vue') },
        { path: 'users', component: () => import('@/views/admin/UsersPage.vue') },
        { path: 'categories', component: () => import('@/views/admin/CategoriesPage.vue') },
      ],
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
