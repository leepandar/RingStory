const BASE_URL = 'http://127.0.0.1:8080'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  loading?: boolean
}

interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

/**
 * 统一 HTTP 请求封装
 */
export function request<T = any>(options: RequestOptions): Promise<T> {
  const { url, method = 'GET', data, header = {}, loading = false } = options

  if (loading) {
    uni.showLoading({ title: '加载中...' })
  }

  // 自动注入 token
  const token = uni.getStorageSync('token')
  if (token) {
    header['Authorization'] = token
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...header
      },
      success: (res) => {
        const result = res.data as ApiResponse<T>
        if (result.code === 200) {
          resolve(result.data)
        } else if (result.code === 401) {
          // token 过期，跳转登录
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({ url: '/pages/login/index' })
          reject(new Error('登录已过期'))
        } else {
          uni.showToast({ title: result.message || '请求失败', icon: 'none' })
          reject(new Error(result.message))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常，请重试', icon: 'none' })
        reject(err)
      },
      complete: () => {
        if (loading) {
          uni.hideLoading()
        }
      }
    })
  })
}

/**
 * 上传文件
 */
export function uploadFile(filePath: string, formData?: Record<string, any>): Promise<any> {
  const token = uni.getStorageSync('token')
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + '/api/album/upload',
      filePath,
      name: 'file',
      formData,
      header: {
        Authorization: token
      },
      success: (res) => {
        const result = JSON.parse(res.data)
        if (result.code === 200) {
          resolve(result.data)
        } else {
          uni.showToast({ title: result.message || '上传失败', icon: 'none' })
          reject(new Error(result.message))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败，请重试', icon: 'none' })
        reject(err)
      }
    })
  })
}
