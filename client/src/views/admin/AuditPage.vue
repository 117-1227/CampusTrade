<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

interface AuditItem { id: number; title: string; description: string; images: string; condition: string; sellerName: string; createdAt: string }

const list = ref<AuditItem[]>([])
const loading = ref(false)

onMounted(loadData)
function loadData() {
  loading.value = true
  api.get('/admin/products/pending').then(r => list.value = r.data.data).finally(() => loading.value = false)
}

function audit(id: number, approved: boolean) {
  const action = approved ? '通过' : '驳回'
  const reason = approved ? null : prompt('驳回原因：')
  if (!approved && reason === null) return
  api.put(`/admin/products/${id}/audit`, { approved, reason }).then(() => {
    ElMessage.success(action + '成功')
    loadData()
  }).catch(e => ElMessage.error(e.response?.data?.message || '操作失败'))
}

function firstImg(images: string) {
  try { return JSON.parse(images)[0] || '' } catch { return '' }
}
</script>

<template>
  <div v-loading="loading">
    <h2>商品审核</h2>
    <el-empty v-if="!list.length && !loading" description="没有待审核商品" />
    <el-table :data="list" stripe>
      <el-table-column label="图片" width="80">
        <template #default="{row}"><el-image v-if="firstImg(row.images)" :src="firstImg(row.images)" style="width:60px;height:60px" fit="cover" /></template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column prop="condition" label="成色" width="80" />
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" type="success" @click="audit(row.id, true)">通过</el-button>
          <el-button size="small" type="danger" @click="audit(row.id, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
