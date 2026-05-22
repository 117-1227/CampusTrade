import { defineStore } from 'pinia'
import { ref } from 'vue'

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  role: 'user' | 'admin'
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))

  // 从 localStorage 恢复 user，避免刷新后丢失（admin 守卫依赖 isAdmin）
  const savedUser = localStorage.getItem('user')
  const user = ref<UserInfo | null>(parseSafe(savedUser))

function parseSafe(raw: string | null): UserInfo | null {
  if (!raw) return null
  try { return JSON.parse(raw) } catch { return null }
}

  function isLoggedIn(): boolean {
    return !!token.value
  }

  function isAdmin(): boolean {
    return user.value?.role === 'admin'
  }

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUser(newUser: UserInfo) {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLoggedIn, isAdmin, setToken, setUser, logout }
})
