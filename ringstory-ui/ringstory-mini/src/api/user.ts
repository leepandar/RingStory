import { request } from '../utils/request'

/** 微信登录 */
export function wxLogin(code: string) {
  return request({ url: '/api/user/wx-login', method: 'POST', data: { code } })
}

/** 获取用户信息 */
export function getUserInfo() {
  return request({ url: '/api/user/info' })
}

/** 更新用户信息 */
export function updateUserInfo(data: { nickName?: string; avatarUrl?: string }) {
  return request({ url: '/api/user/info', method: 'PUT', data })
}
