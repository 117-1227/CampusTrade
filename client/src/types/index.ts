/** 统一 API 返回类型 */
export interface ApiResult<T = unknown> {
  code: number
  data: T
  message: string
}

/** 分页返回 */
export interface PageData<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  phone: string
  campus: string
  role: 'user' | 'admin'
}
