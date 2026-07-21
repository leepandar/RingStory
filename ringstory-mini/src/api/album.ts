import { request } from '../utils/request'

/** 获取时间线照片 */
export function getTimeline(familyId: number, page = 1, size = 20) {
  return request({ url: `/api/album/timeline?familyId=${familyId}&page=${page}&size=${size}` })
}

/** 上传照片 */
export function uploadPhoto(familyId: number, uploaderId: number, filePath: string) {
  const token = uni.getStorageSync('token')
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: 'http://127.0.0.1:8080/api/album/upload',
      filePath,
      name: 'file',
      formData: { familyId: String(familyId), uploaderId: String(uploaderId) },
      header: { Authorization: token },
      success: (res) => {
        const result = JSON.parse(res.data)
        result.code === 200 ? resolve(result.data) : reject(new Error(result.message))
      },
      fail: reject
    })
  })
}

/** 点赞/取消点赞 */
export function toggleLike(photoId: number, userId: number) {
  return request({ url: `/api/album/${photoId}/like?userId=${userId}`, method: 'POST' })
}

/** 获取评论列表 */
export function getComments(photoId: number) {
  return request({ url: `/api/album/${photoId}/comments` })
}

/** 添加评论 */
export function addComment(photoId: number, data: { authorId: number; content: string; parentId?: number }) {
  return request({ url: `/api/album/${photoId}/comment`, method: 'POST', data })
}

/** 创建相册 */
export function createAlbum(data: { familyId: number; name: string; creatorId: number }) {
  return request({ url: '/api/album/album', method: 'POST', data })
}

/** 获取相册列表 */
export function listAlbums(familyId: number) {
  return request({ url: `/api/album/album/list?familyId=${familyId}` })
}
