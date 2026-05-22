<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createProduct, uploadImage } from '@/api/product'
import { getCategories, type CategoryNode } from '@/api/category'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { onMounted } from 'vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const categories = ref<CategoryNode[]>([])
const imageUrls = ref<string[]>([])

const form = reactive({
  title: '', description: '', price: null as number | null,
  originalPrice: null as number | null,
  condition: 'used', categoryId: null as number | null,
})

onMounted(async () => {
  try { const r = await getCategories(); categories.value = r.data.data } catch {}
})

async function onUpload(file: File) {
  try { const r = await uploadImage(file); imageUrls.value.push(r.data.data) }
  catch { ElMessage.error('上传失败') }
}

async function onPublish() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  loading.value = true
  try {
    await createProduct({
      title: form.title, description: form.description,
      price: form.price!, condition: form.condition,
      categoryId: form.categoryId!, images: imageUrls.value,
      originalPrice: form.originalPrice ?? undefined,
    })
    ElMessage.success('发布成功，等待审核')
    router.push('/')
  } catch (e: unknown) { ElMessage.error((e as { response?: { data?: { message?: string } } })?.response?.data?.message || '发布失败')
  } finally { loading.value = false }
}
</script>

<template>
  <div style="max-width:600px;margin:0 auto;padding:20px">
    <h2>发布商品</h2>
    <el-form ref="formRef" :model="form" label-width="80px">
      <el-form-item label="标题" prop="title" :rules="[{required:true,message:'请输入标题'}]">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="描述" prop="description" :rules="[{required:true,message:'请输入描述'}]">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="售价" prop="price" :rules="[{required:true,message:'请输入价格'}]">
        <el-input-number v-model="form.price" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="原价">
        <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="成色">
        <el-select v-model="form.condition">
          <el-option label="全新" value="brand_new" />
          <el-option label="几乎全新" value="like_new" />
          <el-option label="使用过" value="used" />
          <el-option label="有磨损" value="worn" />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="categoryId" :rules="[{required:true,message:'请选择分类'}]">
        <el-select v-model="form.categoryId" placeholder="选择分类">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload :before-upload="(f:any) => { onUpload(f); return false }" list-type="picture-card">
          <el-icon><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="onPublish">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
