import { defineStore } from 'pinia'
import { ref } from 'vue'
import http from '@/utils/request'

interface AdminUser {
  id: number
  username: string
  nickName: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminUser = ref<AdminUser | null>(null)

  async function login(username: string, password: string, captchaToken: string): Promise<boolean> {
    try {
      const result = await http.post<any, any>('/user/admin-login', { username, password, captchaToken })
      token.value = result.token || result
      localStorage.setItem('admin_token', token.value)
      if (result.adminUser) {
        adminUser.value = result.adminUser
      }
      return true
    } catch (e) {
      console.error('登录失败', e)
      return false
    }
  }

  function logout() {
    token.value = ''
    adminUser.value = null
    localStorage.removeItem('admin_token')
  }

  function isLoggedIn(): boolean {
    return !!token.value
  }

  return { token, adminUser, login, logout, isLoggedIn }
})
