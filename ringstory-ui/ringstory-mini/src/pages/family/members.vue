<template>
  <view class="members-page">
    <view class="header">
      <text class="header-title">{{ familyName }}的成员</text>
      <text class="member-count">共 {{ members.length }} 人</text>
    </view>

    <view class="member-list">
      <view v-for="member in members" :key="member.id" class="member-item">
        <image :src="member.avatarUrl || '/static/placeholder.png'" class="avatar" />
        <view class="member-info">
          <text class="member-name">{{ member.nickName }}</text>
          <text class="member-role">{{ member.role === 'OWNER' ? '户主' : '成员' }}</text>
        </view>
        <text class="join-time">{{ member.joinTime }}</text>
      </view>
    </view>

    <button class="btn-invite" @click="goInvite">邀请更多家人</button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { getMembers, getFamily } from '../../api/family'

const userStore = useUserStore()
const familyName = ref('')
const members = ref<any[]>([])

onLoad(async () => {
  if (!userStore.familyId) return
  try {
    const family = await getFamily(userStore.familyId) as any
    familyName.value = family?.name || '我的家庭'
    const list = await getMembers(userStore.familyId) as any
    members.value = Array.isArray(list) ? list : []
  } catch (e) {
    console.error('加载成员失败', e)
  }
})

function goInvite() {
  uni.navigateTo({ url: '/pages/family/invite' })
}
</script>

<style scoped>
.members-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  background: #fff;
}

.header-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.member-count {
  font-size: 24rpx;
  color: #999;
}

.member-list {
  padding: 20rpx 30rpx;
}

.member-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.member-info {
  flex: 1;
}

.member-name {
  font-size: 30rpx;
  color: #333;
  display: block;
}

.member-role {
  font-size: 22rpx;
  color: #4a90d9;
  margin-top: 6rpx;
  display: block;
}

.join-time {
  font-size: 22rpx;
  color: #ccc;
}

.btn-invite {
  margin: 40rpx 30rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 32rpx;
  border: none;
}
</style>
