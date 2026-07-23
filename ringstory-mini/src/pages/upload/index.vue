<template>
  <view class="upload-page">
    <view class="header">
      <text class="header-title">刻录新章</text>
    </view>

    <!-- 选择照片区域 -->
    <view v-if="selectedPhotos.length === 0" class="select-area" @click="choosePhotos">
      <text class="select-icon">&#x2795;</text>
      <text class="select-text">从相册选择照片</text>
      <text class="select-tip">最多可选择 50 张照片</text>
    </view>

    <!-- 已选照片列表 -->
    <view v-else class="photo-list">
      <view class="selected-header">
        <text>已选择 {{ selectedPhotos.length }} 张</text>
        <text class="add-more" @click="choosePhotos">+ 继续添加</text>
      </view>

      <view class="photo-grid">
        <view v-for="(photo, idx) in selectedPhotos" :key="idx" class="photo-item">
          <image :src="photo.path" mode="aspectFill" class="thumb" />
          <view v-if="photo.status === 'uploading'" class="progress-mask">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: photo.progress + '%' }" />
            </view>
            <text class="progress-text">{{ photo.progress }}%</text>
          </view>
          <view v-if="photo.status === 'success'" class="success-mask">
            <text class="success-icon">&#x2714;</text>
          </view>
          <view v-if="photo.status === 'pending'" class="remove-btn" @click="removePhoto(idx)">
            <text class="remove-icon">&#x2716;</text>
          </view>
        </view>
      </view>

      <!-- 压缩策略 -->
      <view class="compress-option">
        <text class="option-label">智能压缩</text>
        <switch :checked="compressEnabled" @change="onCompressChange" color="#4a90d9" />
      </view>

      <!-- 上传按钮 -->
      <button class="btn-upload" :loading="uploading" :disabled="!hasPending" @click="startUpload">
        {{ uploading ? '上传中...' : '开始上传' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { uploadPhoto } from '../../api/album'

const userStore = useUserStore()

interface SelectedPhoto {
  path: string
  status: 'pending' | 'uploading' | 'success' | 'error'
  progress: number
}

const selectedPhotos = ref<SelectedPhoto[]>([])
const compressEnabled = ref(true)
const uploading = ref(false)

const hasPending = computed(() => selectedPhotos.value.some(p => p.status === 'pending'))

function onCompressChange(e: any) {
  compressEnabled.value = e.detail.value
}

function choosePhotos() {
  uni.chooseImage({
    count: 50 - selectedPhotos.value.length,
    sizeType: compressEnabled.value ? ['compressed'] : ['original'],
    sourceType: ['album'],
    success: (res) => {
      const newPhotos: SelectedPhoto[] = res.tempFilePaths.map(path => ({
        path,
        status: 'pending' as const,
        progress: 0
      }))
      selectedPhotos.value.push(...newPhotos)
    }
  })
}

function removePhoto(idx: number) {
  selectedPhotos.value.splice(idx, 1)
}

async function startUpload() {
  if (!userStore.familyId || !userStore.userInfo) return
  uploading.value = true

  const pending = selectedPhotos.value.filter(p => p.status === 'pending')
  for (const photo of pending) {
    photo.status = 'uploading'
    photo.progress = 0

    try {
      // 模拟进度更新
      const progressTimer = setInterval(() => {
        if (photo.progress < 90) {
          photo.progress += 10
        }
      }, 200)

      await uploadPhoto(userStore.familyId, userStore.userInfo.id, photo.path)

      clearInterval(progressTimer)
      photo.status = 'success'
      photo.progress = 100
    } catch (e) {
      photo.status = 'error'
      console.error('上传失败', e)
    }
  }

  uploading.value = false
  uni.showToast({ title: '上传完成', icon: 'success' })

  // 2秒后清空已上传列表
  setTimeout(() => {
    selectedPhotos.value = selectedPhotos.value.filter(p => p.status !== 'success')
  }, 2000)
}
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  padding: 20rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 20rpx);
  background: #fff;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.select-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 40rpx 30rpx;
  padding: 100rpx 0;
  background: #fff;
  border-radius: 16rpx;
  border: 2rpx dashed #ccc;
}

.select-icon {
  font-size: 60rpx;
  color: #4a90d9;
  margin-bottom: 20rpx;
}

.select-text {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 12rpx;
}

.select-tip {
  font-size: 24rpx;
  color: #999;
}

.photo-list {
  padding: 20rpx 30rpx;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #333;
}

.add-more {
  color: #4a90d9;
  font-size: 26rpx;
}

.photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.photo-item {
  width: calc(33.33% - 12rpx);
  aspect-ratio: 1;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}

.thumb {
  width: 100%;
  height: 100%;
}

.progress-mask,
.success-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.progress-bar {
  width: 80%;
  height: 6rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #4a90d9;
  transition: width 0.2s;
}

.progress-text {
  font-size: 22rpx;
  color: #fff;
  margin-top: 8rpx;
}

.success-icon {
  font-size: 48rpx;
  color: #52c41a;
}

.remove-btn {
  position: absolute;
  top: 4rpx;
  right: 4rpx;
  width: 36rpx;
  height: 36rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-icon {
  font-size: 20rpx;
  color: #fff;
}

.compress-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 30rpx 0;
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
}

.option-label {
  font-size: 28rpx;
  color: #333;
}

.btn-upload {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 32rpx;
  border: none;
  margin-top: 20rpx;
}

.btn-upload[disabled] {
  background: #ccc;
}
</style>
