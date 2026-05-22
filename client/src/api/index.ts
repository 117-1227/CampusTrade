import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

// 请求拦截器：注入 JWT Authorization header
api.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

// 响应拦截器：统一错误提示
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || '网络错误，请稍后重试'
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      auth.logout()
    }
    console.error('[API Error]', message)
    return Promise.reject(error)
  },
)

export default api
