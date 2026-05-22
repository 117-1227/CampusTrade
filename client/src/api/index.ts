import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true,
})

// 响应拦截器：统一错误提示
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || '网络错误，请稍后重试'
    console.error('[API Error]', message)
    return Promise.reject(error)
  },
)

export default api
