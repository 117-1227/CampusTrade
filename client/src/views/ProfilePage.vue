<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getMyProducts } from '@/api/product'
import { getFavorites } from '@/api/favorite'
import { getMyProfile } from '@/api/user'
import { getOrdersAsBuyer } from '@/api/order'
import type { ProductItem } from '@/api/product'
import type { OrderItem } from '@/api/order'
import type { UserInfo } from '@/types'

const user = ref<UserInfo | null>(null)
const myProducts = ref<ProductItem[]>([])
const favorites = ref<ProductItem[]>([])
const orders = ref<OrderItem[]>([])
const activeTab = ref('products')

onMounted(async () => {
  try { const r = await getMyProfile(); user.value = r.data.data } catch {}
  try { const r = await getMyProducts(); myProducts.value = r.data.data } catch {}
  try { const r = await getFavorites(); favorites.value = r.data.data } catch {}
  try { const r = await getOrdersAsBuyer(); orders.value = r.data.data } catch {}
})
</script>

<template>
  <div style="max-width:800px;margin:0 auto;padding:20px">
    <h2>个人中心</h2>
    <p v-if="user">{{ user.username }} — {{ user.nickname }}</p>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="我的发布" name="products">
        <el-empty v-if="!myProducts.length" description="暂无发布" />
        <p v-for="p in myProducts" :key="p.id">{{ p.title }} — ¥{{ p.price }}</p>
      </el-tab-pane>
      <el-tab-pane label="我的收藏" name="favs">
        <el-empty v-if="!favorites.length" description="暂无收藏" />
        <p v-for="p in favorites" :key="p.id">{{ p.title }}</p>
      </el-tab-pane>
      <el-tab-pane label="我的订单" name="orders">
        <el-empty v-if="!orders.length" description="暂无订单" />
        <p v-for="o in orders" :key="o.id">#{{ o.id }} — {{ o.status }}</p>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
