<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSellerInfo } from '@/api/user'
import type { UserInfo } from '@/types'
import type { ProductItem } from '@/api/product'

const route = useRoute()
const router = useRouter()
const seller = ref<UserInfo | null>(null)
const products = ref<ProductItem[]>([])

onMounted(async () => {
  try {
    const id = Number(route.params.id)
    const r = await getSellerInfo(id)
    seller.value = r.data.data.seller
    products.value = r.data.data.products
  } catch {}
})
</script>

<template>
  <div style="max-width:800px;margin:0 auto;padding:20px">
    <div v-if="seller">
      <h2>{{ seller.nickname || seller.username }} 的主页</h2>
      <p>{{ seller.campus }}</p>
    </div>
    <h3>在售商品</h3>
    <el-empty v-if="!products.length" description="暂无在售商品" />
    <p v-for="p in products" :key="p.id" @click="router.push(`/product/${p.id}`)" style="cursor:pointer">
      {{ p.title }} — ¥{{ p.price }}
    </p>
  </div>
</template>
