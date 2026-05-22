import api from './index'
import type { ApiResult } from '@/types'

export interface CategoryNode {
  id: number
  name: string
  icon: string
  children: CategoryNode[]
}

export function getCategories() {
  return api.get<ApiResult<CategoryNode[]>>('/categories')
}
