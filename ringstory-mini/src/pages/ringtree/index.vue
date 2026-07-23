<template>
  <view class="ringtree-page">
    <!-- 顶部标题 -->
    <view class="top-bar">
      <text class="bar-title">年轮图谱</text>
      <text class="bar-action" @click="refreshTree">刷新</text>
    </view>

    <!-- 未加入家庭 -->
    <view v-if="!userStore.familyId" class="empty-state">
      <text class="empty-icon">&#x1F333;</text>
      <text class="empty-text">加入家庭后查看年轮图谱</text>
    </view>

    <!-- 年轮树内容 -->
    <scroll-view v-else scroll-y class="tree-scroll" @scrolltolower="loadMoreYears">
      <view v-if="loading && treeData.length === 0" class="loading-tip">
        <text>加载中...</text>
      </view>

      <view v-for="yearNode in treeData" :key="yearNode.year" class="year-node">
        <view class="year-header" @click="toggleYear(yearNode)">
          <text class="year-icon">{{ yearNode.expanded ? '🌿' : '🌱' }}</text>
          <text class="year-label">{{ yearNode.year }}年</text>
          <text class="year-count">{{ yearNode.photoCount }}张</text>
          <text class="year-arrow">{{ yearNode.expanded ? '▼' : '▶' }}</text>
        </view>

        <view v-if="yearNode.expanded" class="month-list">
          <view
            v-for="monthNode in yearNode.children"
            :key="monthNode.label"
            class="month-node"
            @click="goMonthPhotos(yearNode.year, monthNode)"
          >
            <view class="month-dot" />
            <text class="month-label">{{ monthNode.label }}</text>
            <text class="month-count">{{ monthNode.photoCount }}张</text>
          </view>
        </view>
      </view>

      <view v-if="!loading && treeData.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F333;</text>
        <text class="empty-text">还没有照片，年轮树为空</text>
      </view>

      <view v-if="loading && treeData.length > 0" class="loading-tip">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { request } from '../../utils/request'

const userStore = useUserStore()

interface MonthNode {
  label: string
  photoCount: number
  month?: number
}

interface YearNode {
  year: number
  photoCount: number
  expanded: boolean
  children: MonthNode[]
}

const treeData = ref<YearNode[]>([])
const loading = ref(false)

async function fetchTree() {
  if (!userStore.familyId || loading.value) return
  loading.value = true
  try {
    const result = await request<any>({
      url: `/api/ringtree/tree?familyId=${userStore.familyId}`
    }) as any
    // 后端返回 { years: [{year, photoCount, months: [{label, photoCount, month}]}] }
    const years = result?.years || result || []
    treeData.value = years.map((y: any) => ({
      year: y.year,
      photoCount: y.photoCount || 0,
      expanded: false,
      children: (y.months || []).map((m: any) => ({
        label: m.label || `${m.month}月`,
        photoCount: m.photoCount || 0,
        month: m.month
      }))
    }))
  } catch (e) {
    console.error('加载年轮树失败', e)
  } finally {
    loading.value = false
  }
}

function toggleYear(node: YearNode) {
  node.expanded = !node.expanded
}

function goMonthPhotos(year: number, monthNode: MonthNode) {
  const month = monthNode.month || 1
  uni.navigateTo({
    url: `/pages/index/index?year=${year}&month=${month}`
  })
}

function refreshTree() {
  fetchTree()
}

function loadMoreYears() {
  // 年轮树一次性返回，无需分页
}

onShow(() => {
  fetchTree()
})
</script>

<style scoped>
.ringtree-page {
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

.bar-action {
  font-size: 26rpx;
  color: #4a90d9;
}

.tree-scroll {
  height: calc(100vh - 120rpx);
  padding: 20rpx;
}

.year-node {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.year-header {
  display: flex;
  align-items: center;
  padding: 30rpx;
}

.year-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.year-label {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.year-count {
  font-size: 24rpx;
  color: #999;
  margin-right: 16rpx;
}

.year-arrow {
  font-size: 24rpx;
  color: #ccc;
}

.month-list {
  padding: 0 30rpx 20rpx 60rpx;
  border-top: 1rpx solid #f0f0f0;
}

.month-node {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
}

.month-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #4a90d9;
  margin-right: 20rpx;
}

.month-label {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}

.month-count {
  font-size: 24rpx;
  color: #999;
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
