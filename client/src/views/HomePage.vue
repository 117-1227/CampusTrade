<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProducts, type ProductItem } from '@/api/product'
import { getCategories, type CategoryNode } from '@/api/category'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const products = ref<ProductItem[]>([])
const categories = ref<CategoryNode[]>([])
const keyword = ref('')
const categoryId = ref<number | undefined>()
const loading = ref(false)
const error = ref('')
let requestSeq = 0

async function loadProducts() {
  loading.value = true
  error.value = ''
  const seq = ++requestSeq
  try {
    const res = await getProducts({
      keyword: keyword.value || undefined,
      categoryId: categoryId.value,
    })
    if (seq !== requestSeq) return
    products.value = res.data.data || []
  } catch {
    if (seq !== requestSeq) return
    error.value = '加载失败，请检查后端是否启动'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = res.data.data || []
  } catch { /* categories are optional */ }
  // 从 URL query 读取外部搜索（如 AppHeader 跳转）
  const kw = route.query.keyword as string
  if (kw) keyword.value = kw
  loadProducts()
})

watch(categoryId, () => loadProducts())

function onSearch() { loadProducts() }
function goDetail(id: number) { router.push(`/product/${id}`) }

function safeFirstImage(images: string): string {
  try { return JSON.parse(images)[0] || '' } catch { return '' }
}

const conditionLabels: Record<string, string> = {
  brand_new: '全新', like_new: '几乎全新', used: '使用过', worn: '有磨损',
}
</script>

<template>
  <div class="home">
    <div class="hero">
      <h1>CampusTrade 校园二手交易平台</h1>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索商品..." size="large" :prefix-icon="Search" clearable
          @keyup.enter="onSearch" @clear="onSearch" style="width: 400px" />
        <el-button type="primary" size="large" :icon="Search" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <div class="content">
      <div class="category-nav">
        <el-button
          :type="!categoryId ? 'primary' : 'default'"
          size="small"
          @click="categoryId = undefined"
        >全部</el-button>
        <template v-for="cat in categories" :key="cat.id">
          <el-button
            :type="categoryId === cat.id ? 'primary' : 'default'"
            size="small"
            @click="categoryId = cat.id"
          >{{ cat.name }}</el-button>
          <el-button
            v-for="sub in cat.children"
            :key="sub.id"
            :type="categoryId === sub.id ? 'primary' : 'default'"
            size="small"
            @click="categoryId = sub.id"
            style="margin-left: 0"
          >{{ sub.name }}</el-button>
        </template>
      </div>

      <div v-loading="loading" class="product-grid">
        <el-empty v-if="!loading && !error && products.length === 0" description="暂无商品" />
        <el-alert v-if="error" :title="error" type="error" show-icon closable @close="error=''" />

        <div v-for="p in products" :key="p.id" class="product-card" @click="goDetail(p.id)">
          <div class="card-img">
            <el-image
              v-if="safeFirstImage(p.images)"
              :src="safeFirstImage(p.images)"
              fit="cover"
              style="width:100%;height:180px"
            >
              <template #error><div class="img-placeholder">暂无图片</div></template>
            </el-image>
            <div v-else class="img-placeholder">暂无图片</div>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ p.title }}</h3>
            <div class="card-price">
              <span class="price">¥{{ p.price }}</span>
              <span v-if="p.originalPrice" class="original">¥{{ p.originalPrice }}</span>
            </div>
            <div class="card-meta">
              <el-tag size="small">{{ conditionLabels[p.condition] || p.condition }}</el-tag>
              <span class="seller">{{ p.sellerName }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home { min-height: 100vh; background: #f5f5f5; }
.hero {
  text-align: center; padding: 48px 16px 32px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  color: #fff;
}
.hero h1 { margin: 0 0 20px; font-size: 28px; }
.search-bar { display: flex; justify-content: center; gap: 8px; }
.content { max-width: 1200px; margin: 0 auto; padding: 20px; }
.category-nav { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 20px; }
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  min-height: 200px;
}
.product-card {
  background: #fff; border-radius: 8px; overflow: hidden;
  cursor: pointer; transition: box-shadow .2s;
}
.product-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,.15); }
.img-placeholder {
  width: 100%; height: 180px; background: #eee;
  display: flex; align-items: center; justify-content: center;
  color: #999; font-size: 14px;
}
.card-body { padding: 12px; }
.card-title { margin: 0 0 8px; font-size: 15px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.original { color: #999; font-size: 13px; text-decoration: line-through; margin-left: 8px; }
.card-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.seller { color: #999; font-size: 12px; }
</style>
