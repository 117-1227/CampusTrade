import api from './index'
import type { ApiResult } from '@/types'

export interface ProductItem {
  id: number
  title: string
  description: string
  price: number
  originalPrice: number | null
  condition: string
  images: string
  categoryId: number
  sellerId: number
  sellerName: string
  status: string
  auditStatus: string
  viewCount: number
  favCount: number
  createdAt: string
}

export interface ProductDetail extends ProductItem {
  categoryName: string
  sellerAvatar: string
}

export function getProducts(params?: { keyword?: string; categoryId?: number; page?: number; pageSize?: number }) {
  return api.get<ApiResult<ProductItem[]>>('/products', { params })
}

export function getProductById(id: number) {
  return api.get<ApiResult<ProductDetail>>(`/products/${id}`)
}

export function createProduct(data: {
  title: string
  description: string
  price: number
  originalPrice?: number
  condition: string
  images: string[]
  categoryId: number
}) {
  return api.post<ApiResult<ProductItem>>('/products', data)
}

export function updateProduct(id: number, data: Record<string, unknown>) {
  return api.put<ApiResult<ProductItem>>(`/products/${id}`, data)
}

export function deleteProduct(id: number) {
  return api.delete<ApiResult<null>>(`/products/${id}`)
}

export function getMyProducts() {
  return api.get<ApiResult<ProductItem[]>>('/products/my')
}

export function uploadImage(file: File) {
  const form = new FormData()
  form.append('file', file)
  return api.post<ApiResult<string>>('/upload/image', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
