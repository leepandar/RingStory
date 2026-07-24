<template>
  <view class="create-page">
    <view class="form-area">
      <text class="form-title">创建你的家庭</text>
      <text class="form-desc">给家庭起个名字，开始记录美好时光</text>

      <view class="input-group">
        <text class="label">家庭名称</text>
        <input v-model="familyName" placeholder="例如：张家大院" class="input" maxlength="20" />
      </view>

      <button class="btn-create" :disabled="!familyName.trim()" @click="handleCreate">
        创建家庭
      </button>
    </view>

    <!-- 或者加入已有家庭 -->
    <view class="divider">
      <view class="line" />
      <text class="divider-text">或</text>
      <view class="line" />
    </view>

    <view class="join-area">
      <button class="btn-join" @click="goJoin">通过邀请码加入</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '../../stores/user'
import { createFamily } from '../../api/family'

const userStore = useUserStore()
const familyName = ref('')

async function handleCreate() {
  if (!familyName.value.trim() || !userStore.userInfo) return
  try {
    const result = await createFamily(familyName.value, userStore.userInfo.id) as any
    const familyId = result?.id || result
    userStore.setFamily(familyId)
    uni.showToast({ title: '创建成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1000)
  } catch (e) {
    console.error('创建失败', e)
  }
}

function goJoin() {
  uni.navigateTo({ url: '/pages/family/join' })
}
</script>

<style scoped>
.create-page {
  min-height: 100vh;
  background: #fff;
  padding: 60rpx;
}

.form-area {
  margin-top: 80rpx;
}

.form-title {
  font-size: 44rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.form-desc {
  font-size: 28rpx;
  color: #999;
  display: block;
  margin-bottom: 60rpx;
}

.input-group {
  margin-bottom: 40rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
  display: block;
  margin-bottom: 16rpx;
}

.input {
  width: 100%;
  height: 88rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
}

.btn-create {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 32rpx;
  border: none;
}

.btn-create[disabled] {
  background: #ccc;
}

.divider {
  display: flex;
  align-items: center;
  margin: 60rpx 0;
}

.line {
  flex: 1;
  height: 1rpx;
  background: #e8e8e8;
}

.divider-text {
  padding: 0 30rpx;
  font-size: 26rpx;
  color: #999;
}

.btn-join {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #fff;
  color: #4a90d9;
  font-size: 32rpx;
  border: 2rpx solid #4a90d9;
}
</style>
