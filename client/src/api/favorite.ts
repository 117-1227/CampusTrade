import api from './index'
import type { ApiResult } from '@/types'
import type { ProductItem } from './product'

export function toggleFavorite(productId: number) {
  return api.post<ApiResult<boolean>>(`/products/${productId}/favorite`)
}

export function getFavorites() {
  return api.get<ApiResult<ProductItem[]>>('/favorites')
}

export function checkFavorited(productId: number) {
  return api.get<ApiResult<boolean>>(`/products/${productId}/favorite`)
}
