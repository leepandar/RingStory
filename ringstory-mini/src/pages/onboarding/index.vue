<template>
  <view class="onboarding">
    <swiper class="swiper" :current="currentPage" @change="onSwiperChange">
      <!-- 第一屏: Ring -->
      <swiper-item>
        <view class="slide">
          <view class="icon-circle">
            <text class="icon-text">&#x25EF;</text>
          </view>
          <text class="title">时光成环</text>
          <text class="desc">每一张照片，都是年轮的一圈。\n记录家庭点滴，汇聚成独一无二的时光年轮。</text>
        </view>
      </swiper-item>
      <!-- 第二屏: Story -->
      <swiper-item>
        <view class="slide">
          <view class="icon-circle">
            <text class="icon-text">&#x2606;</text>
          </view>
          <text class="title">故事如歌</text>
          <text class="desc">按时间轴浏览家庭照片，\n每一天都值得被珍藏。</text>
        </view>
      </swiper-item>
      <!-- 第三屏: Together -->
      <swiper-item>
        <view class="slide">
          <view class="icon-circle">
            <text class="icon-text">&#x2665;</text>
          </view>
          <text class="title">一起记录</text>
          <text class="desc">邀请家人加入，\n共同编织属于你们的家庭年轮。</text>
        </view>
      </swiper-item>
    </swiper>

    <!-- 指示器 -->
    <view class="dots">
      <view v-for="i in 3" :key="i" class="dot" :class="{ active: currentPage === i - 1 }" />
    </view>

    <!-- 按钮 -->
    <view class="btn-area">
      <button v-if="currentPage < 2" class="btn-next" @click="nextSlide">下一步</button>
      <button v-else class="btn-start" @click="goLogin">开启家庭年轮</button>
      <text v-if="currentPage < 2" class="skip" @click="goLogin">跳过</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const currentPage = ref(0)

function onSwiperChange(e: any) {
  currentPage.value = e.detail.current
}

function nextSlide() {
  if (currentPage.value < 2) {
    currentPage.value++
  }
}

function goLogin() {
  uni.setStorageSync('onboarded', 'true')
  uni.navigateTo({ url: '/pages/login/index' })
}
</script>

<style scoped>
.onboarding {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #e8f0fe 0%, #ffffff 100%);
}

.swiper {
  flex: 1;
}

.slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0 60rpx;
}

.icon-circle {
  width: 200rpx;
  height: 200rpx;
  border-radius: 50%;
  background: #4a90d9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 60rpx;
}

.icon-text {
  font-size: 80rpx;
  color: #ffffff;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.desc {
  font-size: 28rpx;
  color: #666;
  text-align: center;
  line-height: 1.8;
}

.dots {
  display: flex;
  justify-content: center;
  padding: 40rpx 0 20rpx;
}

.dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #ccc;
  margin: 0 10rpx;
}

.dot.active {
  background: #4a90d9;
  width: 32rpx;
  border-radius: 8rpx;
}

.btn-area {
  padding: 30rpx 60rpx 80rpx;
  position: relative;
}

.btn-next,
.btn-start {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  color: #fff;
  background: #4a90d9;
  border: none;
}

.skip {
  position: absolute;
  right: 60rpx;
  top: 50rpx;
  font-size: 28rpx;
  color: #999;
}
</style>
