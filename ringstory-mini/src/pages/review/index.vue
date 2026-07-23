<template>
  <view class="review-page">
    <view class="top-bar">
      <text class="bar-title">年轮放映室</text>
    </view>

    <view v-if="!userStore.familyId" class="empty-state">
      <text class="empty-icon">&#x1F3AC;</text>
      <text class="empty-text">加入家庭后查看回顾</text>
    </view>

    <scroll-view v-else scroll-y class="review-scroll">
      <!-- 回顾列表 -->
      <view
        v-for="review in reviews"
        :key="review.id"
        class="review-card"
        @click="playReview(review)"
      >
        <image
          :src="review.coverUrl || '/static/placeholder.png'"
          mode="aspectFill"
          class="review-cover"
        />
        <view class="review-overlay">
          <text class="play-icon">&#x25B6;</text>
        </view>
        <view class="review-info">
          <text class="review-title">{{ review.title }}</text>
          <text class="review-desc">{{ review.type }} · {{ review.photoCount }}张照片</text>
          <text class="review-time">{{ review.period }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading-tip"><text>加载中...</text></view>
      <view v-if="!loading && reviews.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F3AC;</text>
        <text class="empty-text">暂无回顾</text>
        <text class="empty-sub">系统会每月自动生成回顾</text>
      </view>
    </scroll-view>

    <!-- 播放弹窗 -->
    <view v-if="playing" class="player-modal" @click.stop="closePlayer">
      <view class="player-content" @click.stop>
        <image
          v-if="currentReview?.posterUrl"
          :src="currentReview.posterUrl"
          mode="aspectFit"
          class="player-poster"
        />
        <view v-else class="player-placeholder">
          <text class="placeholder-icon">&#x1F3AC;</text>
          <text class="placeholder-text">{{ currentReview?.title }}</text>
          <text class="placeholder-sub">视频生成中，敬请期待</text>
        </view>
        <view class="player-photos">
          <scroll-view scroll-x class="photo-strip">
            <image
              v-for="photo in currentReview?.photos || []"
              :key="photo.id"
              :src="photo.thumbUrl"
              mode="aspectFill"
              class="strip-photo"
              @click.stop="goDetail(photo.id)"
            />
          </scroll-view>
        </view>
        <text class="close-player" @click="closePlayer">&#x2715; 关闭</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { request } from '../../utils/request'

const userStore = useUserStore()

interface ReviewPhoto {
  id: number
  thumbUrl: string
}

interface ReviewItem {
  id: number
  title: string
  type: string
  photoCount: number
  period: string
  coverUrl: string
  posterUrl: string
  videoUrl: string
  photos: ReviewPhoto[]
}

const reviews = ref<ReviewItem[]>([])
const loading = ref(false)
const playing = ref(false)
const currentReview = ref<ReviewItem | null>(null)

async function fetchReviews() {
  if (!userStore.familyId || loading.value) return
  loading.value = true
  try {
    const result = await request<any[]>({
      url: `/api/review/list?familyId=${userStore.familyId}`
    }) as any
    reviews.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载回顾失败', e)
  } finally {
    loading.value = false
  }
}

function playReview(review: ReviewItem) {
  currentReview.value = review
  playing.value = true
}

function closePlayer() {
  playing.value = false
  currentReview.value = null
}

function goDetail(photoId: number) {
  uni.navigateTo({ url: `/pages/photo/detail?id=${photoId}` })
}

onShow(() => {
  fetchReviews()
})
</script>

<style scoped>
.review-page {
  min-height: 100vh;
  background: #1a1a2e;
}

.top-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 20rpx);
  background: #1a1a2e;
}

.bar-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.review-scroll {
  height: calc(100vh - 120rpx);
  padding: 20rpx;
}

.review-card {
  position: relative;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 30rpx;
  height: 360rpx;
}

.review-cover {
  width: 100%;
  height: 100%;
}

.review-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  font-size: 80rpx;
  color: rgba(255, 255, 255, 0.9);
}

.review-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 30rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
}

.review-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  display: block;
  margin-bottom: 8rpx;
}

.review-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-bottom: 4rpx;
}

.review-time {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.6);
}

.player-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.player-content {
  width: 90%;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.player-poster {
  width: 100%;
  border-radius: 16rpx;
}

.player-placeholder {
  width: 100%;
  height: 400rpx;
  background: #2a2a3e;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.placeholder-text {
  font-size: 32rpx;
  color: #fff;
  margin-bottom: 10rpx;
}

.placeholder-sub {
  font-size: 24rpx;
  color: #999;
}

.player-photos {
  margin-top: 20rpx;
  width: 100%;
}

.photo-strip {
  white-space: nowrap;
}

.strip-photo {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 12rpx;
  display: inline-block;
}

.close-player {
  margin-top: 30rpx;
  font-size: 28rpx;
  color: #fff;
  padding: 16rpx 40rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 30rpx;
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

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.empty-sub {
  font-size: 24rpx;
  color: #666;
}
</style>
