<script setup lang="ts">
import { onMounted, ref } from 'vue'
import api from '@/api'

const stats = ref({ totalUsers: 0, totalProducts: 0, pendingAudits: 0, approvedProducts: 0 })
onMounted(async () => {
  try { const r = await api.get('/admin/dashboard'); stats.value = r.data.data } catch {}
})
</script>

<template>
  <div>
    <h2>管理概览</h2>
    <el-row :gutter="20">
      <el-col :span="6"><el-card shadow="hover"><h3>{{ stats.totalUsers }}</h3><p>总用户数</p></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><h3>{{ stats.totalProducts }}</h3><p>总商品数</p></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><h3 style="color:#e6a23c">{{ stats.pendingAudits }}</h3><p>待审核</p></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><h3 style="color:#67c23a">{{ stats.approvedProducts }}</h3><p>已通过</p></el-card></el-col>
    </el-row>
  </div>
</template>
