import { request } from '../utils/request'

/** 创建家庭 */
export function createFamily(name: string, userId: number) {
  return request({ url: '/api/family', method: 'POST', data: { name, userId } })
}

/** 获取家庭详情 */
export function getFamily(id: number) {
  return request({ url: `/api/family/${id}` })
}

/** 获取成员列表 */
export function getMembers(familyId: number) {
  return request({ url: `/api/family/${familyId}/members` })
}

/** 创建邀请 */
export function createInvitation(familyId: number, userId: number) {
  return request({ url: `/api/family/${familyId}/invitation?userId=${userId}`, method: 'POST' })
}

/** 通过邀请加入家庭 */
export function joinFamily(token: string, userId: number) {
  return request({ url: `/api/family/join?token=${token}&userId=${userId}`, method: 'POST' })
}
