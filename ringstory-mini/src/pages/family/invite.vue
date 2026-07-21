<template>
  <view class="invite-page">
    <view class="invite-card">
      <text class="card-title">邀请家人加入</text>
      <text class="card-desc">分享邀请链接或小程序码给家人</text>

      <!-- 邀请码 -->
      <view v-if="invitationToken" class="token-area">
        <text class="token-label">邀请码</text>
        <text class="token-value">{{ invitationToken }}</text>
        <button class="btn-copy" @click="copyToken">复制邀请码</button>
      </view>

      <!-- 操作按钮 -->
      <view class="action-btns">
        <button class="btn-share" open-type="share">分享给微信好友</button>
        <button class="btn-qrcode" @click="generateQrCode">生成小程序码</button>
      </view>
    </view>

    <!-- 已发出的邀请 -->
    <view class="invite-history">
      <text class="section-title">已发出的邀请</text>
      <view v-for="inv in invitations" :key="inv.id" class="invite-item">
        <view class="invite-info">
          <text class="invite-code">邀请码: {{ inv.token }}</text>
          <text class="invite-status" :class="inv.status">{{ statusText(inv.status) }}</text>
        </view>
        <text class="invite-time">{{ inv.createTime }}</text>
      </view>
      <view v-if="invitations.length === 0" class="no-invite">
        <text>暂无邀请记录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { createInvitation } from '../../api/family'

const userStore = useUserStore()
const invitationToken = ref('')
const invitations = ref<any[]>([])

onLoad(async () => {
  await createNewInvitation()
})

async function createNewInvitation() {
  if (!userStore.familyId || !userStore.userInfo) return
  try {
    const result = await createInvitation(userStore.familyId, userStore.userInfo.id) as any
    invitationToken.value = result?.token || result
  } catch (e) {
    console.error('创建邀请失败', e)
  }
}

function copyToken() {
  uni.setClipboardData({
    data: invitationToken.value,
    success: () => {
      uni.showToast({ title: '已复制', icon: 'success' })
    }
  })
}

function generateQrCode() {
  uni.showToast({ title: '小程序码生成中...', icon: 'none' })
  // TODO: 调用后端生成小程序码
}

function statusText(status: string): string {
  const map: Record<string, string> = {
    PENDING: '待使用',
    USED: '已使用',
    EXPIRED: '已过期'
  }
  return map[status] || status
}

onShareAppMessage(() => ({
  title: '快来加入我们的家庭年轮吧！',
  path: `/pages/family/join?token=${invitationToken.value}`
}))
</script>

<style scoped>
.invite-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.invite-card {
  margin: 30rpx;
  padding: 40rpx;
  background: #fff;
  border-radius: 16rpx;
  text-align: center;
}

.card-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
}

.card-desc {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-bottom: 40rpx;
}

.token-area {
  background: #f8f8f8;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.token-label {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-bottom: 12rpx;
}

.token-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #4a90d9;
  letter-spacing: 8rpx;
  display: block;
  margin-bottom: 20rpx;
}

.btn-copy {
  width: 240rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 32rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 26rpx;
  border: none;
}

.action-btns {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.btn-share,
.btn-qrcode {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  border: none;
}

.btn-share {
  background: #07c160;
  color: #fff;
}

.btn-qrcode {
  background: #fff;
  color: #4a90d9;
  border: 2rpx solid #4a90d9;
}

.invite-history {
  margin: 30rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.invite-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}

.invite-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.invite-code {
  font-size: 26rpx;
  color: #333;
}

.invite-status {
  font-size: 22rpx;
  padding: 4rpx 16rpx;
  border-radius: 16rpx;
}

.invite-status.PENDING {
  background: #e6f7ff;
  color: #4a90d9;
}

.invite-status.USED {
  background: #f6ffed;
  color: #52c41a;
}

.invite-status.EXPIRED {
  background: #f5f5f5;
  color: #999;
}

.invite-time {
  font-size: 22rpx;
  color: #ccc;
  margin-top: 8rpx;
  display: block;
}

.no-invite {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #ccc;
}
</style>
