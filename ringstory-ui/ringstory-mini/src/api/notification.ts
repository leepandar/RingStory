import { request } from '../utils/request'

/** 获取通知列表 */
export function getNotificationList(page = 1, size = 20) {
  return request({ url: `/api/notification/list?page=${page}&size=${size}` })
}

/** 标记单条通知已读 */
export function markRead(id: number) {
  return request({ url: `/api/notification/${id}/read`, method: 'PUT' })
}

/** 全部标记已读 */
export function markAllRead() {
  return request({ url: '/api/notification/read-all', method: 'PUT' })
}
