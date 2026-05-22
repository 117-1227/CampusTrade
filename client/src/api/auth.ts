import api from './index'
import type { ApiResult, UserInfo } from '@/types'

export interface LoginResult {
  token: string
  user: UserInfo
}

export function register(data: {
  username: string
  password: string
  nickname: string
  phone?: string
}) {
  return api.post<ApiResult<UserInfo>>('/auth/register', data)
}

export function login(data: { username: string; password: string }) {
  return api.post<ApiResult<LoginResult>>('/auth/login', data)
}

export function adminLogin(data: { username: string; password: string }) {
  return api.post<ApiResult<LoginResult>>('/admin/auth/login', data)
}

export function getMe() {
  return api.get<ApiResult<UserInfo>>('/auth/me')
}
