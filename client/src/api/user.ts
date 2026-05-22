import api from './index'
import type { ApiResult, UserInfo } from '@/types'
import type { ProductItem } from './product'

export function getMyProfile() {
  return api.get<ApiResult<UserInfo>>('/users/me')
}

export function updateProfile(data: Partial<UserInfo>) {
  return api.put<ApiResult<UserInfo>>('/users/me', data)
}

export function getSellerInfo(id: number) {
  return api.get<ApiResult<{ seller: UserInfo; products: ProductItem[] }>>(`/users/${id}/profile`)
}
