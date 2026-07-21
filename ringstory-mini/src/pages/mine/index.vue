<template>
  <view class="mine-page">
    <!-- 个人信息头部 -->
    <view class="profile-header">
      <view class="avatar-area" @click="editAvatar">
        <image :src="userStore.userInfo?.avatarUrl || '/static/placeholder.png'" class="avatar" />
        <text class="edit-hint">点击修改</text>
      </view>
      <view class="user-info">
        <text class="nick-name" @click="editNickname">
          {{ userStore.userInfo?.nickName || '未设置昵称' }}
        </text>
        <text v-if="familyName" class="family-name">{{ familyName }}</text>
        <text v-else class="no-family" @click="goCreateFamily">创建或加入家庭</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-group">
      <view class="menu-item" @click="goMembers">
        <text class="menu-icon">&#x1F46A;</text>
        <text class="menu-text">家庭成员</text>
        <text class="menu-arrow">&#x276F;</text>
      </view>
      <view class="menu-item" @click="goInvite">
        <text class="menu-icon">&#x2709;</text>
        <text class="menu-text">邀请家人</text>
        <text class="menu-arrow">&#x276F;</text>
      </view>
    </view>

    <view class="menu-group">
      <view class="menu-item" @click="toggleCareMode">
        <text class="menu-icon">&#x1F50D;</text>
        <text class="menu-text">关怀模式（大字）</text>
        <switch :checked="careMode" @change="toggleCareMode" color="#4a90d9" />
      </view>
      <view class="menu-item" @click="goNotification">
        <text class="menu-icon">&#x1F514;</text>
        <text class="menu-text">通知偏好</text>
        <text class="menu-arrow">&#x276F;</text>
      </view>
    </view>

    <view class="menu-group">
      <view class="menu-item" @click="feedback">
        <text class="menu-icon">&#x1F4DD;</text>
        <text class="menu-text">意见反馈</text>
        <text class="menu-arrow">&#x276F;</text>
      </view>
      <view class="menu-item" @click="about">
        <text class="menu-icon">&#x2139;</text>
        <text class="menu-text">关于 RingStory</text>
        <text class="menu-arrow">&#x276F;</text>
      </view>
    </view>

    <button class="btn-logout" @click="handleLogout">退出登录</button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { getFamily } from '../../api/family'

const userStore = useUserStore()
const familyName = ref('')
const careMode = ref(false)

onShow(async () => {
  if (userStore.familyId) {
    try {
      const family = await getFamily(userStore.familyId) as any
      familyName.value = family?.name || ''
    } catch (e) { /* ignore */ }
  }
  careMode.value = uni.getStorageSync('careMode') === 'true'
})

function editAvatar() {
  uni.chooseImage({
    count: 1,
    sourceType: ['album', 'camera'],
    success: (res) => {
      // TODO: 上传头像到后端
      uni.showToast({ title: '头像更新中...', icon: 'none' })
    }
  })
}

function editNickname() {
  uni.showModal({
    title: '修改昵称',
    editable: true,
    placeholderText: '请输入新昵称',
    success: async (res) => {
      if (res.confirm && res.content) {
        try {
          const { updateUserInfo } = await import('../../api/user')
          await updateUserInfo({ nickName: res.content })
          if (userStore.userInfo) {
            userStore.userInfo.nickName = res.content
            uni.setStorageSync('userInfo', JSON.stringify(userStore.userInfo))
          }
          uni.showToast({ title: '修改成功', icon: 'success' })
        } catch (e) {
          console.error('修改昵称失败', e)
        }
      }
    }
  })
}

function goMembers() {
  uni.navigateTo({ url: '/pages/family/members' })
}

function goInvite() {
  uni.navigateTo({ url: '/pages/family/invite' })
}

function goCreateFamily() {
  uni.navigateTo({ url: '/pages/family/create' })
}

function goNotification() {
  uni.navigateTo({ url: '/pages/notification/index' })
}

function toggleCareMode() {
  careMode.value = !careMode.value
  uni.setStorageSync('careMode', careMode.value ? 'true' : '')
  uni.showToast({ title: careMode.value ? '已开启关怀模式' : '已关闭关怀模式', icon: 'none' })
}

function feedback() {
  // #ifdef MP-WEIXIN
  // 微信小程序使用内置反馈页
  uni.openCustomerServiceChat?.({
    corpId: '',
    url: '',
    fail: () => {
      uni.showToast({ title: '请在设置中反馈', icon: 'none' })
    }
  })
  // #endif
  // #ifndef MP-WEIXIN
  uni.showToast({ title: '请发送邮件至 feedback@ringstory.com', icon: 'none' })
  // #endif
}

function about() {
  uni.showModal({
    title: 'RingStory',
    content: '版本 1.0.0\n\n记录家庭时光的年轮',
    showCancel: false
  })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}
</script>

<style scoped>
.mine-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.profile-header {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 40rpx);
  background: #fff;
}

.avatar-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
}

.edit-hint {
  font-size: 20rpx;
  color: #999;
  margin-top: 8rpx;
}

.user-info {
  flex: 1;
}

.nick-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.family-name {
  font-size: 26rpx;
  color: #4a90d9;
  display: block;
}

.no-family {
  font-size: 26rpx;
  color: #ff9800;
  display: block;
}

.menu-group {
  margin-top: 20rpx;
  background: #fff;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.menu-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 24rpx;
  color: #ccc;
}

.btn-logout {
  margin: 60rpx 30rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #fff;
  color: #ff4d4f;
  font-size: 32rpx;
  border: none;
}
</style>
