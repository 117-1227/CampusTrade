<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductById, type ProductDetail } from '@/api/product'
import { toggleFavorite } from '@/api/favorite'
import { createConversation } from '@/api/chat'
import { createOrder } from '@/api/order'
import { useAuthStore } from '@/stores/authStore'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const product = ref<ProductDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const id = Number(route.params.id)
    const res = await getProductById(id)
    product.value = res.data.data
  } catch { product.value = null } finally { loading.value = false }
})

async function onContact() {
  if (!auth.isLoggedIn()) { router.push('/login'); return }
  try {
    const res = await createConversation(product.value!.id)
    router.push(`/chat/${res.data.data.id}`)
  } catch { ElMessage.error('发起会话失败') }
}

async function onOrder() {
  if (!auth.isLoggedIn()) { router.push('/login'); return }
  try {
    await createOrder(product.value!.id)
    ElMessage.success('下单成功')
  } catch (e: unknown) { ElMessage.error((e as { response?: { data?: { message?: string } } })?.response?.data?.message || '下单失败') }
}

async function onFav() {
  if (!auth.isLoggedIn()) { router.push('/login'); return }
  try {
    await toggleFavorite(product.value!.id)
    ElMessage.success('已操作')
  } catch { ElMessage.error('操作失败') }
}

function safeImages(images: string): string[] {
  try { return JSON.parse(images) } catch { return [] }
}
</script>

<template>
  <div v-loading="loading" style="max-width:800px;margin:0 auto;padding:20px">
    <el-empty v-if="!loading && !product" description="商品不存在" />
    <template v-if="product">
      <div v-for="img in safeImages(product.images)" :key="img">
        <el-image :src="img" fit="contain" style="max-height:400px" />
      </div>
      <h1>{{ product.title }}</h1>
      <p style="font-size:24px;color:#f56c6c;margin:16px 0">¥{{ product.price }}</p>
      <p v-if="product.originalPrice" style="color:#999;text-decoration:line-through">原价 ¥{{ product.originalPrice }}</p>
      <p style="white-space:pre-wrap">{{ product.description }}</p>
      <div style="margin-top:20px;display:flex;gap:12px">
        <el-button type="primary" @click="onContact">联系卖家</el-button>
        <el-button @click="onFav">收藏</el-button>
        <el-button type="success" @click="onOrder">立即下单</el-button>
      </div>
    </template>
  </div>
</template>
