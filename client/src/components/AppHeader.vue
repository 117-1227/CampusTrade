<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <el-header style="height:56px;background:#fff;border-bottom:1px solid #eee;display:flex;align-items:center;justify-content:space-between;padding:0 24px">
    <div style="display:flex;align-items:center;gap:24px">
      <span style="font-size:18px;font-weight:bold;color:#409eff;cursor:pointer" @click="router.push('/')">CampusTrade</span>
    </div>
    <div style="display:flex;align-items:center;gap:12px">
      <el-button v-if="!auth.isLoggedIn()" text @click="router.push('/login')">登录</el-button>
      <el-button v-if="!auth.isLoggedIn()" text @click="router.push('/register')">注册</el-button>
      <template v-if="auth.isLoggedIn()">
        <el-button text @click="router.push('/publish')">发布商品</el-button>
        <el-button text @click="router.push('/chat')">私信</el-button>
        <el-button text @click="router.push('/orders')">订单</el-button>
        <el-button text @click="router.push('/profile')">{{ auth.user?.nickname || auth.user?.username }}</el-button>
        <el-button text type="danger" @click="logout">退出</el-button>
      </template>
    </div>
  </el-header>
</template>
