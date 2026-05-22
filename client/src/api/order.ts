import api from './index'
import type { ApiResult } from '@/types'

export interface OrderItem {
  id: number
  productId: number
  buyerId: number
  sellerId: number
  status: 'pending' | 'completed' | 'cancelled'
  createdAt: string
  updatedAt: string
}

export function createOrder(productId: number) {
  return api.post<ApiResult<null>>('/orders', { productId })
}

export function updateOrderStatus(id: number, status: string) {
  return api.put<ApiResult<null>>(`/orders/${id}/status`, { status })
}

export function getOrdersAsBuyer() {
  return api.get<ApiResult<OrderItem[]>>('/orders/buy')
}

export function getOrdersAsSeller() {
  return api.get<ApiResult<OrderItem[]>>('/orders/sell')
}
