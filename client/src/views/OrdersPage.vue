<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getOrdersAsBuyer, getOrdersAsSeller, updateOrderStatus, type OrderItem } from '@/api/order'
import { ElMessage } from 'element-plus'

const buyOrders = ref<OrderItem[]>([])
const sellOrders = ref<OrderItem[]>([])
const activeTab = ref('buy')

onMounted(async () => {
  try { const r = await getOrdersAsBuyer(); buyOrders.value = r.data.data } catch {}
  try { const r = await getOrdersAsSeller(); sellOrders.value = r.data.data } catch {}
})

async function confirm(id: number) {
  try { await updateOrderStatus(id, 'completed'); ElMessage.success('已确认成交'); location.reload() }
  catch (e: unknown) { ElMessage.error((e as { response?: { data?: { message?: string } } })?.response?.data?.message || '操作失败') }
}

async function cancel(id: number) {
  try { await updateOrderStatus(id, 'cancelled'); ElMessage.success('已取消'); location.reload() }
  catch (e: unknown) { ElMessage.error((e as { response?: { data?: { message?: string } } })?.response?.data?.message || '操作失败') }
}

const labels: Record<string, string> = { pending: '待处理', completed: '已完成', cancelled: '已取消' }
</script>

<template>
  <div style="max-width:800px;margin:0 auto;padding:20px">
    <h2>我的订单</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="我买的" name="buy">
        <el-empty v-if="!buyOrders.length" description="暂无订单" />
        <div v-for="o in buyOrders" :key="o.id" style="padding:12px;border-bottom:1px solid #eee">
          <span>#{{ o.id }} — {{ labels[o.status] || o.status }}</span>
          <el-button v-if="o.status==='pending'" size="small" type="danger" @click="cancel(o.id)">取消</el-button>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我卖的" name="sell">
        <el-empty v-if="!sellOrders.length" description="暂无订单" />
        <div v-for="o in sellOrders" :key="o.id" style="padding:12px;border-bottom:1px solid #eee">
          <span>#{{ o.id }} — {{ labels[o.status] || o.status }}</span>
          <el-button v-if="o.status==='pending'" size="small" type="success" @click="confirm(o.id)">确认成交</el-button>
          <el-button v-if="o.status==='pending'" size="small" type="danger" @click="cancel(o.id)">取消</el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
