<template>
  <view class="notification-page">
    <!-- 顶部操作栏 -->
    <view class="top-bar">
      <text class="bar-title">年轮回声</text>
      <text v-if="notifications.length > 0" class="read-all" @click="markAllRead">全部已读</text>
    </view>

    <!-- 通知列表 -->
    <scroll-view scroll-y class="notify-scroll" @scrolltolower="loadMore">
      <view
        v-for="item in notifications"
        :key="item.id"
        class="notify-item"
        :class="{ unread: !item.read }"
        @click="handleClick(item)"
      >
        <view class="notify-icon" :class="item.type">
          <text>{{ typeIcon(item.type) }}</text>
        </view>
        <view class="notify-body">
          <text class="notify-title">{{ item.title }}</text>
          <text class="notify-content">{{ item.content }}</text>
          <text class="notify-time">{{ formatTime(item.createTime) }}</text>
        </view>
        <view v-if="!item.read" class="unread-dot" />
      </view>

      <view v-if="loading" class="loading-tip">
        <text>加载中...</text>
      </view>

      <view v-if="!loading && notifications.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F514;</text>
        <text class="empty-text">暂无通知</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '../../utils/request'

interface NotifyItem {
  id: number
  type: string
  title: string
  content: string
  createTime: string
  read: boolean
  targetId?: number
}

const notifications = ref<NotifyItem[]>([])
const loading = ref(false)
const page = ref(1)
const noMore = ref(false)

async function fetchNotifications(reset = false) {
  if (loading.value) return
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  loading.value = true
  try {
    const result = await request<NotifyItem[]>({
      url: `/api/notification/list?page=${page.value}&size=20`
    }) as any
    const list = Array.isArray(result) ? result : (result?.records || [])
    if (list.length < 20) noMore.value = true
    if (reset) {
      notifications.value = list
    } else {
      notifications.value.push(...list)
    }
    page.value++
  } catch (e) {
    console.error('加载通知失败', e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value) fetchNotifications()
}

async function markAllRead() {
  try {
    await request({ url: '/api/notification/read-all', method: 'PUT' })
    notifications.value.forEach(n => n.read = true)
  } catch (e) {
    console.error('标记已读失败', e)
  }
}

async function handleClick(item: NotifyItem) {
  if (!item.read) {
    try {
      await request({ url: `/api/notification/${item.id}/read`, method: 'PUT' })
      item.read = true
    } catch (e) { /* ignore */ }
  }
  // 根据通知类型跳转
  if (item.type === 'INVITATION' && item.targetId) {
    uni.navigateTo({ url: '/pages/family/members' })
  } else if (item.type === 'NEW_PHOTO' && item.targetId) {
    uni.navigateTo({ url: `/pages/photo/detail?id=${item.targetId}` })
  }
}

function typeIcon(type: string): string {
  const map: Record<string, string> = {
    INVITATION: '👥',
    NEW_PHOTO: '📷',
    COMMENT: '💬',
    LIKE: '❤',
    SYSTEM: '📢'
  }
  return map[type] || '📢'
}

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

onShow(() => {
  fetchNotifications(true)
})
</script>

<style scoped>
.notification-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 20rpx);
  background: #fff;
}

.bar-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.read-all {
  font-size: 26rpx;
  color: #4a90d9;
}

.notify-scroll {
  height: calc(100vh - 120rpx);
}

.notify-item {
  display: flex;
  align-items: flex-start;
  padding: 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  position: relative;
}

.notify-item.unread {
  background: #f0f7ff;
}

.notify-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  font-size: 28rpx;
}

.notify-icon.INVITATION {
  background: #e6f7ff;
}

.notify-icon.NEW_PHOTO {
  background: #f6ffed;
}

.notify-icon.COMMENT {
  background: #fff7e6;
}

.notify-icon.LIKE {
  background: #fff1f0;
}

.notify-body {
  flex: 1;
}

.notify-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.notify-content {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notify-time {
  font-size: 22rpx;
  color: #ccc;
}

.unread-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #ff4d4f;
  position: absolute;
  top: 30rpx;
  right: 30rpx;
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
}
</style>
