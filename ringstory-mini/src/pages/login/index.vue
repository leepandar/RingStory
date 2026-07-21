<template>
  <view class="login-page">
    <view class="logo-area">
      <view class="logo-circle">
        <text class="logo-text">Ring</text>
      </view>
      <text class="app-name">RingStory</text>
      <text class="app-slogan">记录家庭时光的年轮</text>
    </view>

    <view class="login-area">
      <button class="btn-wx-login" :loading="loading" @click="handleWxLogin">
        <!-- #ifdef MP-WEIXIN -->
        微信一键登录
        <!-- #endif -->
        <!-- #ifdef H5 -->
        H5 开发模式登录
        <!-- #endif -->
      </button>
      <text class="login-tip">登录即表示同意《用户协议》和《隐私政策》</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const loading = ref(false)

async function handleWxLogin() {
  if (loading.value) return
  loading.value = true
  try {
    const success = await userStore.wxLogin()
    if (success) {
      // 检查是否已加入家庭
      if (userStore.familyId) {
        uni.switchTab({ url: '/pages/index/index' })
      } else {
        uni.navigateTo({ url: '/pages/family/create' })
      }
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #ffffff;
  padding: 0 60rpx;
}

.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 120rpx;
}

.logo-circle {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background: #4a90d9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
}

.logo-text {
  font-size: 48rpx;
  color: #ffffff;
  font-weight: bold;
}

.app-name {
  font-size: 44rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
}

.app-slogan {
  font-size: 28rpx;
  color: #999;
}

.login-area {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.btn-wx-login {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #07c160;
  color: #ffffff;
  font-size: 32rpx;
  border: none;
}

.login-tip {
  margin-top: 30rpx;
  font-size: 22rpx;
  color: #bbb;
}
</style>
