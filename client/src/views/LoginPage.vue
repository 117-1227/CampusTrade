<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/authStore'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    const { token, user } = res.data.data
    authStore.setToken(token)
    authStore.setUser(user)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect as string
    router.push(redirect || (user.role === 'admin' ? '/admin' : '/'))
  } catch (err: unknown) {
    const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <h2 style="text-align: center; margin: 0">登录 CampusTrade</h2>
      </template>

      <el-form
        :model="form"
        label-width="80px"
        size="large"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <p style="text-align: center; color: #999">
        还没有账号？
        <router-link to="/register">去注册</router-link>
      </p>
    </el-card>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f5f5;
}
.auth-card {
  width: 400px;
}
</style>
