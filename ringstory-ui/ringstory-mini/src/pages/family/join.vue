<template>
  <view class="join-page">
    <view class="form-area">
      <text class="form-title">加入家庭</text>
      <text class="form-desc">输入邀请码，加入你的家庭</text>

      <view class="input-group">
        <text class="label">邀请码</text>
        <input
          v-model="inviteToken"
          placeholder="请输入邀请码"
          class="input"
          maxlength="32"
        />
      </view>

      <button class="btn-join" :disabled="!inviteToken.trim()" :loading="joining" @click="handleJoin">
        加入家庭
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { joinFamily } from '../../api/family'

const userStore = useUserStore()
const inviteToken = ref('')
const joining = ref(false)

onLoad((query) => {
  // 从分享链接进入时自动填入邀请码
  if (query?.token) {
    inviteToken.value = query.token
  }
})

async function handleJoin() {
  if (!inviteToken.value.trim() || !userStore.userInfo) return
  joining.value = true
  try {
    const result = await joinFamily(inviteToken.value, userStore.userInfo.id) as any
    const familyId = result?.familyId || result
    if (familyId) {
      userStore.setFamily(familyId)
    }
    uni.showToast({ title: '加入成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1000)
  } catch (e) {
    console.error('加入失败', e)
  } finally {
    joining.value = false
  }
}
</script>

<style scoped>
.join-page {
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

.btn-join {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 32rpx;
  border: none;
}

.btn-join[disabled] {
  background: #ccc;
}
</style>
