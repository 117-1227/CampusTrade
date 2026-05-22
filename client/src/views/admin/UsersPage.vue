<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

interface UserItem { id: number; username: string; nickname: string; phone: string; role: string; status: string; createdAt: string }
const list = ref<UserItem[]>([])
const loading = ref(false)

onMounted(loadData)
function loadData() {
  loading.value = true
  api.get('/admin/users').then(r => list.value = r.data.data.records || r.data.data).finally(() => loading.value = false)
}

function toggle(id: number, status: string) {
  api.put(`/admin/users/${id}/status`, { status }).then(() => {
    ElMessage.success('操作成功')
    loadData()
  }).catch(e => ElMessage.error(e.response?.data?.message || '操作失败'))
}
</script>

<template>
  <div v-loading="loading">
    <h2>用户管理</h2>
    <el-table :data="list" stripe>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="role" label="角色" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.status==='active'?'success':'danger'">{{ row.status === 'active' ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button v-if="row.role!=='admin'" size="small" :type="row.status==='active'?'danger':'success'"
            @click="toggle(row.id, row.status==='active'?'disabled':'active')">
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
