<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

interface CategoryItem { id: number; name: string; icon: string; parentId: number | null; sortOrder: number }
const list = ref<CategoryItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref<CategoryItem | null>(null)
const form = ref({ name: '', icon: '', parentId: null as number | null, sortOrder: 0 })

onMounted(loadData)
function loadData() {
  loading.value = true
  api.get('/admin/categories').then(r => list.value = r.data.data).finally(() => loading.value = false)
}

function openDialog(cat?: CategoryItem) {
  editing.value = cat || null
  form.value = cat ? { name: cat.name, icon: cat.icon, parentId: cat.parentId, sortOrder: cat.sortOrder } : { name: '', icon: '', parentId: null, sortOrder: 0 }
  dialogVisible.value = true
}

function save() {
  const data = { ...form.value }
  const req = editing.value
    ? api.put(`/admin/categories/${editing.value.id}`, data)
    : api.post('/admin/categories', data)
  req.then(() => { dialogVisible.value = false; loadData(); ElMessage.success('保存成功') })
    .catch(e => ElMessage.error(e.response?.data?.message || '保存失败'))
}

function del(id: number) {
  ElMessageBox.confirm('确认删除？', '警告', { type: 'warning' }).then(() => {
    api.delete(`/admin/categories/${id}`).then(() => { loadData(); ElMessage.success('已删除') })
      .catch(e => ElMessage.error(e.response?.data?.message || '删除失败'))
  }).catch(() => {})
}

const parents = computed(() => list.value.filter(c => !c.parentId))
</script>

<template>
  <div v-loading="loading">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2 style="margin:0">分类管理</h2>
      <el-button type="primary" @click="openDialog()">新增分类</el-button>
    </div>
    <el-table :data="list" stripe>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="icon" label="图标" width="100" />
      <el-table-column prop="parentId" label="父分类ID" width="100" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="Element Plus 图标名" /></el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" clearable placeholder="无（一级分类）">
            <el-option v-for="p in parents" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

