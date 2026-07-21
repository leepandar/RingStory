import { defineStore } from 'pinia'
import { ref } from 'vue'
import { request } from '../utils/request'

interface UserInfo {
  id: number
  nickName: string
  avatarUrl: string
  phone: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref<UserInfo | null>(null)
  const familyId = ref<number | null>(null)

  /**
   * 初始化：从本地存储恢复登录态
   */
  function init() {
    token.value = uni.getStorageSync('token') || ''
    const info = uni.getStorageSync('userInfo')
    if (info) {
      userInfo.value = JSON.parse(info)
    }
    familyId.value = uni.getStorageSync('familyId') || null
  }

  /**
   * 微信登录（小程序环境）/ H5 模拟登录（开发调试）
   */
  async function wxLogin(): Promise<boolean> {
    try {
      let code = ''

      // #ifdef MP-WEIXIN
      // 微信小程序环境：真实 wx.login
      const loginRes = await new Promise<UniApp.LoginRes | null>((resolve) => {
        uni.login({
          provider: 'weixin',
          success: (res) => resolve(res),
          fail: () => resolve(null)
        })
      })
      if (!loginRes) {
        uni.showToast({ title: '微信登录失败', icon: 'none' })
        return false
      }
      code = loginRes.code
      // #endif

      // #ifdef H5
      // H5 环境：使用模拟 code 进行开发调试
      code = 'h5_mock_' + Date.now()
      console.log('[H5 开发模式] 使用模拟 code 登录')
      // #endif

      const result = await request<{ token: string; userInfo: UserInfo; isNew: boolean }>({
        url: '/api/user/wx-login',
        method: 'POST',
        data: { code }
      })

      token.value = result.token
      userInfo.value = result.userInfo
      uni.setStorageSync('token', result.token)
      uni.setStorageSync('userInfo', JSON.stringify(result.userInfo))

      return true
    } catch (e) {
      console.error('登录失败', e)
      return false
    }
  }

  /**
   * 设置当前家庭
   */
  function setFamily(id: number) {
    familyId.value = id
    uni.setStorageSync('familyId', id)
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    userInfo.value = null
    familyId.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('familyId')
    uni.reLaunch({ url: '/pages/login/index' })
  }

  /**
   * 是否已登录
   */
  function isLoggedIn(): boolean {
    return !!token.value
  }

  return {
    token,
    userInfo,
    familyId,
    init,
    wxLogin,
    setFamily,
    logout,
    isLoggedIn
  }
})
