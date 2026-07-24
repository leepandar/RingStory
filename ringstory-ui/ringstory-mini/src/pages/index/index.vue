<template>
  <view class="home">
    <!-- 顶部标题栏 -->
    <view class="header">
      <text class="header-title">时光年轮</text>
      <view class="header-right" @click="goNotification">
        <text class="bell-icon">&#x1F514;</text>
        <view v-if="unreadCount > 0" class="badge">
          <text class="badge-text">{{ unreadCount > 99 ? '99+' : unreadCount }}</text>
        </view>
      </view>
    </view>

    <!-- 未加入家庭引导 -->
    <view v-if="!userStore.familyId" class="empty-guide">
      <text class="empty-icon">&#x1F3E0;</text>
      <text class="empty-title">还没有加入家庭</text>
      <text class="empty-desc">创建或加入家庭，开始记录美好时光</text>
      <view class="empty-btns">
        <button class="btn-create" :disabled="navLoading" @click="goCreateFamily">创建家庭</button>
        <button class="btn-join" :disabled="navLoading" @click="goJoinFamily">加入家庭</button>
      </view>
    </view>

    <!-- 瀑布流照片网格 -->
    <scroll-view
      v-else
      class="photo-scroll"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <!-- 日期分割 -->
      <view v-for="(group, gIdx) in photoGroups" :key="gIdx" class="date-group">
        <view class="date-divider">
          <text class="date-text">{{ group.label }}</text>
        </view>
        <view class="waterfall">
          <view
            v-for="(photo, pIdx) in group.photos"
            :key="photo.id"
            class="waterfall-item"
            @click="goPhotoDetail(photo.id)"
          >
            <image
              :src="photo.thumbUrl || '/static/placeholder.png'"
              mode="widthFix"
              class="photo-img"
              lazy-load
            />
            <view class="photo-overlay">
              <image
                v-if="photo.uploaderAvatar"
                :src="photo.uploaderAvatar"
                class="uploader-avatar"
              />
            </view>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-tip">
        <text>加载中...</text>
      </view>
      <view v-else-if="noMore" class="loading-tip">
        <text>没有更多了</text>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && photoGroups.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F4F7;</text>
        <text class="empty-desc">还没有照片，快去刻录新章吧</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { getTimeline } from '../../api/album'

const userStore = useUserStore()

interface PhotoItem {
  id: number
  thumbUrl: string
  uploaderAvatar: string
  shootTime: string
}

interface PhotoGroup {
  label: string
  photos: PhotoItem[]
}

const photos = ref<PhotoItem[]>([])
const page = ref(1)
const loading = ref(false)
const noMore = ref(false)
const refreshing = ref(false)
const unreadCount = ref(0)
const navLoading = ref(false)

const photoGroups = computed(() => {
  const groups: Record<string, PhotoItem[]> = {}
  for (const photo of photos.value) {
    const dateKey = formatDateLabel(photo.shootTime)
    if (!groups[dateKey]) {
      groups[dateKey] = []
    }
    groups[dateKey].push(photo)
  }
  return Object.entries(groups).map(([label, items]) => ({ label, photos: items }))
})

function formatDateLabel(dateStr: string): string {
  if (!dateStr) return '未知日期'
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) return '今天'
  if (date.toDateString() === yesterday.toDateString()) return '昨天'
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

async function fetchPhotos(reset = false) {
  if (!userStore.familyId || loading.value) return
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  loading.value = true
  try {
    const result = await getTimeline(userStore.familyId, page.value) as any
    const list: PhotoItem[] = Array.isArray(result) ? result : (result?.records || [])
    if (list.length < 20) {
      noMore.value = true
    }
    if (reset) {
      photos.value = list
    } else {
      photos.value.push(...list)
    }
    page.value++
  } catch (e) {
    console.error('加载照片失败', e)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  fetchPhotos(true)
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    fetchPhotos()
  }
}

onPullDownRefresh(async () => {
  await fetchPhotos(true)
  uni.stopPullDownRefresh()
})

onShow(() => {
  if (userStore.familyId) {
    fetchPhotos(true)
  }
})

function goPhotoDetail(photoId: number) {
  uni.navigateTo({ url: `/pages/photo/detail?id=${photoId}` })
}

function goNotification() {
  uni.navigateTo({ url: '/pages/notification/index' })
}

function goCreateFamily() {
  if (navLoading.value) return
  navLoading.value = true
  uni.navigateTo({
    url: '/pages/family/create',
    complete: () => { navLoading.value = false }
  })
}

function goJoinFamily() {
  if (navLoading.value) return
  navLoading.value = true
  uni.navigateTo({
    url: '/pages/family/join',
    complete: () => { navLoading.value = false }
  })
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 20rpx);
  background: #ffffff;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.header-right {
  position: relative;
  padding: 10rpx;
}

.bell-icon {
  font-size: 40rpx;
}

.badge {
  position: absolute;
  top: 0;
  right: -10rpx;
  background: #ff4d4f;
  border-radius: 16rpx;
  padding: 0 8rpx;
  min-width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-text {
  font-size: 18rpx;
  color: #fff;
}

.empty-guide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 60rpx;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 30rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.empty-btns {
  display: flex;
  gap: 20rpx;
}

.btn-create,
.btn-join {
  padding: 0 40rpx;
  height: 72rpx;
  line-height: 72rpx;
  border-radius: 36rpx;
  font-size: 28rpx;
}

.btn-create {
  background: #4a90d9;
  color: #fff;
  border: none;
}

.btn-join {
  background: #fff;
  color: #4a90d9;
  border: 2rpx solid #4a90d9;
}

.photo-scroll {
  height: calc(100vh - 120rpx);
}

.date-group {
  margin-bottom: 20rpx;
}

.date-divider {
  padding: 16rpx 30rpx;
  background: #f0f0f0;
}

.date-text {
  font-size: 24rpx;
  color: #999;
  font-weight: 500;
}

.waterfall {
  display: flex;
  flex-wrap: wrap;
  padding: 10rpx 20rpx;
}

.waterfall-item {
  width: calc(33.33% - 16rpx);
  margin: 8rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #fff;
  position: relative;
}

.photo-img {
  width: 100%;
  display: block;
}

.photo-overlay {
  position: absolute;
  right: 8rpx;
  bottom: 8rpx;
}

.uploader-avatar {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  border: 2rpx solid #fff;
}

.loading-tip {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}
</style>
