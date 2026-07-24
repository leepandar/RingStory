<template>
  <view class="notify-settings-page">
    <view class="top-bar">
      <text class="bar-title">通知偏好</text>
    </view>

    <scroll-view scroll-y class="settings-scroll">
      <!-- 全局开关 -->
      <view class="setting-section">
        <view class="section-header">
          <text class="section-title">全局设置</text>
        </view>
        <view class="setting-item">
          <text class="setting-label">接收通知</text>
          <switch :checked="!settings.muteAll" @change="toggleMuteAll" color="#4a90d9" />
        </view>
      </view>

      <!-- 免打扰时段 -->
      <view class="setting-section">
        <view class="section-header">
          <text class="section-title">免打扰时段</text>
          <switch :checked="settings.quietHoursEnabled" @change="toggleQuietHours" color="#4a90d9" />
        </view>
        <view v-if="settings.quietHoursEnabled" class="time-range">
          <picker mode="time" :value="settings.quietStart" @change="onStartChange">
            <text class="time-picker">{{ settings.quietStart }}</text>
          </picker>
          <text class="time-sep">至</text>
          <picker mode="time" :value="settings.quietEnd" @change="onEndChange">
            <text class="time-picker">{{ settings.quietEnd }}</text>
          </picker>
        </view>
      </view>

      <!-- 各类型开关 -->
      <view class="setting-section">
        <view class="section-header">
          <text class="section-title">通知类型</text>
        </view>
        <view class="setting-item">
          <text class="setting-label">新照片上传</text>
          <switch :checked="settings.newPhoto" @change="toggleType('newPhoto')" color="#4a90d9" />
        </view>
        <view class="setting-item">
          <text class="setting-label">评论通知</text>
          <switch :checked="settings.comment" @change="toggleType('comment')" color="#4a90d9" />
        </view>
        <view class="setting-item">
          <text class="setting-label">点赞通知</text>
          <switch :checked="settings.like" @change="toggleType('like')" color="#4a90d9" />
        </view>
        <view class="setting-item">
          <text class="setting-label">家庭邀请</text>
          <switch :checked="settings.invitation" @change="toggleType('invitation')" color="#4a90d9" />
        </view>
        <view class="setting-item">
          <text class="setting-label">回顾生成</text>
          <switch :checked="settings.review" @change="toggleType('review')" color="#4a90d9" />
        </view>
        <view class="setting-item">
          <text class="setting-label">系统通知</text>
          <switch :checked="settings.system" @change="toggleType('system')" color="#4a90d9" />
        </view>
      </view>

      <!-- 保存按钮 -->
      <view class="save-section">
        <button class="save-btn" @click="saveSettings">保存设置</button>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '../../utils/request'

interface NotifySettings {
  muteAll: boolean
  quietHoursEnabled: boolean
  quietStart: string
  quietEnd: string
  newPhoto: boolean
  comment: boolean
  like: boolean
  invitation: boolean
  review: boolean
  system: boolean
}

const settings = reactive<NotifySettings>({
  muteAll: false,
  quietHoursEnabled: false,
  quietStart: '22:00',
  quietEnd: '08:00',
  newPhoto: true,
  comment: true,
  like: true,
  invitation: true,
  review: true,
  system: true
})

const loading = ref(false)

async function fetchSettings() {
  loading.value = true
  try {
    const result = await request<any>({
      url: '/api/notification/setting'
    }) as any
    if (result) {
      settings.muteAll = result.muteAll || false
      settings.quietHoursEnabled = result.quietHoursEnabled || false
      settings.quietStart = result.quietStart || '22:00'
      settings.quietEnd = result.quietEnd || '08:00'
      settings.newPhoto = result.newPhoto !== false
      settings.comment = result.comment !== false
      settings.like = result.like !== false
      settings.invitation = result.invitation !== false
      settings.review = result.review !== false
      settings.system = result.system !== false
    }
  } catch (e) {
    console.error('加载通知设置失败', e)
  } finally {
    loading.value = false
  }
}

function toggleMuteAll(e: any) {
  settings.muteAll = !e.detail.value
}

function toggleQuietHours() {
  settings.quietHoursEnabled = !settings.quietHoursEnabled
}

function toggleType(key: keyof NotifySettings) {
  ;(settings as any)[key] = !(settings as any)[key]
}

function onStartChange(e: any) {
  settings.quietStart = e.detail.value
}

function onEndChange(e: any) {
  settings.quietEnd = e.detail.value
}

async function saveSettings() {
  try {
    await request({
      url: '/api/notification/setting',
      method: 'POST',
      data: {
        muteAll: settings.muteAll,
        quietHoursEnabled: settings.quietHoursEnabled,
        quietStart: settings.quietStart,
        quietEnd: settings.quietEnd,
        newPhoto: settings.newPhoto,
        comment: settings.comment,
        like: settings.like,
        invitation: settings.invitation,
        review: settings.review,
        system: settings.system
      }
    })
    uni.showToast({ title: '保存成功' })
  } catch (e) {
    console.error('保存设置失败', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

onShow(() => {
  fetchSettings()
})
</script>

<style scoped>
.notify-settings-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.top-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 20rpx);
  background: #fff;
}

.bar-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.settings-scroll {
  height: calc(100vh - 120rpx);
}

.setting-section {
  background: #fff;
  margin-bottom: 20rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 30rpx 0;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.setting-label {
  font-size: 28rpx;
  color: #333;
}

.time-range {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 30rpx 30rpx;
  gap: 20rpx;
}

.time-picker {
  background: #f5f5f5;
  padding: 12rpx 30rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333;
}

.time-sep {
  font-size: 28rpx;
  color: #999;
}

.save-section {
  padding: 40rpx 30rpx;
}

.save-btn {
  background: #4a90d9;
  color: #fff;
  border: none;
  border-radius: 36rpx;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
}
</style>
