<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/admin/login')
}

const menuItems = [
  { path: '/admin', label: '概览' },
  { path: '/admin/audit', label: '商品审核' },
  { path: '/admin/users', label: '用户管理' },
  { path: '/admin/categories', label: '分类管理' },
]
</script>

<template>
  <div style="display:flex;min-height:100vh">
    <div style="width:200px;background:#304156;color:#fff;padding:16px 0">
      <h3 style="text-align:center;margin-bottom:24px">CampusTrade</h3>
      <div
        v-for="m in menuItems" :key="m.path"
        :style="{
          padding:'12px 24px',cursor:'pointer',
          background: route.path===m.path ? '#409eff' : 'transparent'
        }"
        @click="router.push(m.path)"
      >{{ m.label }}</div>
    </div>
    <div style="flex:1;background:#f5f5f5">
      <div style="height:48px;background:#fff;display:flex;align-items:center;justify-content:flex-end;padding:0 16px;border-bottom:1px solid #eee">
        <span style="margin-right:16px">管理员</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
      <div style="padding:20px">
        <router-view />
      </div>
    </div>
  </div>
</template>
