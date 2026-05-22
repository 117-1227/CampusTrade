<script setup lang="ts">
import { onMounted, ref } from 'vue'
import api from '@/api'

const message = ref('')

onMounted(async () => {
  try {
    const res = await api.get('/hello')
    message.value = res.data?.data ?? '已连接后端'
  } catch {
    message.value = '后端未启动，仅前端可用'
  }
})
</script>

<template>
  <div style="padding: 40px; text-align: center">
    <h1>CampusTrade 校园二手交易平台</h1>
    <p style="color: #999">{{ message }}</p>
  </div>
</template>
