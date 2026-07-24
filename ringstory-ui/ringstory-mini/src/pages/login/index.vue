<template>
  <view class="login-page">
    <!-- 背景装饰 -->
    <view class="bg-layer">
      <view class="bg-gradient" />
      <!-- 年轮装饰环 -->
      <view class="ring-deco ring-1" />
      <view class="ring-deco ring-2" />
      <view class="ring-deco ring-3" />
      <view class="ring-deco ring-4" />
      <!-- 浮动光点 -->
      <view v-for="i in 8" :key="i" class="dot" :class="'dot-' + i" />
    </view>

    <!-- 主内容 -->
    <view class="main-content">
      <!-- Logo 区域 -->
      <view class="logo-section">
        <view class="logo-outer">
          <view class="logo-middle">
            <view class="logo-inner">
              <text class="logo-icon">&#x1F333;</text>
            </view>
          </view>
        </view>
        <text class="app-name">RingStory</text>
        <view class="slogan-wrap">
          <text class="slogan-line">时光为轮</text>
          <text class="slogan-dot">·</text>
          <text class="slogan-line">记忆成书</text>
        </view>
      </view>

      <!-- 功能亮点 -->
      <view class="highlights">
        <view class="highlight-item">
          <text class="hl-icon">📷</text>
          <text class="hl-text">智能整理</text>
        </view>
        <view class="hl-divider" />
        <view class="highlight-item">
          <text class="hl-icon">🌳</text>
          <text class="hl-text">年轮图谱</text>
        </view>
        <view class="hl-divider" />
        <view class="highlight-item">
          <text class="hl-icon">🎬</text>
          <text class="hl-text">时光放映</text>
        </view>
      </view>

      <!-- 登录按钮区域 -->
      <view class="login-section">
        <button
          class="btn-login"
          :loading="loading"
          :disabled="loading"
          @click="handleWxLogin"
        >
          <text v-if="!loading" class="btn-icon">&#x1F4AC;</text>
          <!-- #ifdef MP-WEIXIN -->
          <text>{{ loading ? '登录中...' : '微信一键登录' }}</text>
          <!-- #endif -->
          <!-- #ifdef H5 -->
          <text>{{ loading ? '登录中...' : 'H5 开发模式登录' }}</text>
          <!-- #endif -->
        </button>

        <view class="agreement-row">
          <text class="agree-text">登录即表示同意</text>
          <text class="agree-link">《用户协议》</text>
          <text class="agree-text">和</text>
          <text class="agree-link">《隐私政策》</text>
        </view>
      </view>
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
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ==================== 背景层 ==================== */
.bg-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, #0d1117 0%, #161b22 35%, #1a2332 65%, #0f1923 100%);
}

/* 年轮装饰环 */
.ring-deco {
  position: absolute;
  border-radius: 50%;
  border: 1rpx solid rgba(88, 166, 255, 0.08);
  left: 50%;
  top: 45%;
  transform: translate(-50%, -50%);
}

.ring-1 { width: 300rpx; height: 300rpx; animation: ringBreath 6s ease-in-out infinite; }
.ring-2 { width: 500rpx; height: 500rpx; animation: ringBreath 6s ease-in-out infinite 0.5s; }
.ring-3 { width: 700rpx; height: 700rpx; animation: ringBreath 6s ease-in-out infinite 1s; }
.ring-4 { width: 900rpx; height: 900rpx; animation: ringBreath 6s ease-in-out infinite 1.5s; }

@keyframes ringBreath {
  0%, 100% { opacity: 0.3; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 1; transform: translate(-50%, -50%) scale(1.02); }
}

/* 浮动光点 */
.dot {
  position: absolute;
  border-radius: 50%;
  background: rgba(88, 166, 255, 0.35);
  animation: dotFloat 7s ease-in-out infinite;
}

.dot-1 { width: 8rpx; height: 8rpx; left: 15%; top: 20%; animation-delay: 0s; }
.dot-2 { width: 6rpx; height: 6rpx; left: 80%; top: 15%; animation-delay: 1s; }
.dot-3 { width: 10rpx; height: 10rpx; left: 70%; top: 60%; animation-delay: 2s; }
.dot-4 { width: 5rpx; height: 5rpx; left: 25%; top: 70%; animation-delay: 3s; }
.dot-5 { width: 7rpx; height: 7rpx; left: 60%; top: 35%; animation-delay: 0.5s; }
.dot-6 { width: 4rpx; height: 4rpx; left: 40%; top: 80%; animation-delay: 1.5s; }
.dot-7 { width: 9rpx; height: 9rpx; left: 85%; top: 45%; animation-delay: 2.5s; }
.dot-8 { width: 6rpx; height: 6rpx; left: 10%; top: 50%; animation-delay: 4s; }

@keyframes dotFloat {
  0%, 100% { transform: translateY(0); opacity: 0.2; }
  50% { transform: translateY(-20rpx); opacity: 0.7; }
}

/* ==================== 主内容 ==================== */
.main-content {
  position: relative;
  z-index: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 60rpx;
}

/* ==================== Logo 区域 ==================== */
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
}

.logo-outer {
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(88, 166, 255, 0.15), rgba(163, 113, 247, 0.15));
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  animation: logoGlow 4s ease-in-out infinite;
}

@keyframes logoGlow {
  0%, 100% { box-shadow: 0 0 30rpx rgba(88, 166, 255, 0.1); }
  50% { box-shadow: 0 0 50rpx rgba(88, 166, 255, 0.25); }
}

.logo-middle {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(88, 166, 255, 0.2), rgba(163, 113, 247, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-inner {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #58a6ff, #a371f7);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  font-size: 48rpx;
}

.app-name {
  font-size: 48rpx;
  font-weight: 800;
  color: #e6edf3;
  letter-spacing: 4rpx;
  margin-bottom: 16rpx;
}

.slogan-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.slogan-line {
  font-size: 26rpx;
  color: rgba(230, 237, 243, 0.5);
  letter-spacing: 2rpx;
}

.slogan-dot {
  font-size: 26rpx;
  color: #58a6ff;
}

/* ==================== 功能亮点 ==================== */
.highlights {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 80rpx;
  padding: 20rpx 36rpx;
  background: rgba(255, 255, 255, 0.04);
  border: 1rpx solid rgba(255, 255, 255, 0.06);
  border-radius: 40rpx;
  backdrop-filter: blur(10px);
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.hl-icon {
  font-size: 28rpx;
}

.hl-text {
  font-size: 24rpx;
  color: rgba(230, 237, 243, 0.7);
}

.hl-divider {
  width: 1rpx;
  height: 28rpx;
  background: rgba(255, 255, 255, 0.12);
}

/* ==================== 登录按钮 ==================== */
.login-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.btn-login {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  background: linear-gradient(135deg, #07c160, #06ad56);
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  box-shadow: 0 8rpx 32rpx rgba(7, 193, 96, 0.3);
  transition: all 0.3s;
}

.btn-login:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 16rpx rgba(7, 193, 96, 0.2);
}

.btn-icon {
  font-size: 32rpx;
}

.agreement-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 32rpx;
}

.agree-text {
  font-size: 22rpx;
  color: rgba(230, 237, 243, 0.3);
}

.agree-link {
  font-size: 22rpx;
  color: #58a6ff;
}
</style>
