<template>
  <view class="person-page">
    <view class="top-bar">
      <text class="bar-title">人物相册</text>
    </view>

    <view v-if="!userStore.familyId" class="empty-state">
      <text class="empty-icon">&#x1F464;</text>
      <text class="empty-text">加入家庭后查看人物相册</text>
    </view>

    <scroll-view v-else scroll-y class="person-scroll">
      <!-- 人物列表 -->
      <view class="cluster-list">
        <view
          v-for="cluster in clusters"
          :key="cluster.id"
          class="cluster-item"
          :class="{ active: selectedCluster === cluster.id }"
          @click="selectCluster(cluster)"
        >
          <image
            :src="cluster.coverUrl || '/static/placeholder.png'"
            mode="aspectFill"
            class="cluster-cover"
          />
          <view class="cluster-info">
            <text class="cluster-name">{{ cluster.name || '未命名' }}</text>
            <text class="cluster-count">{{ cluster.photoCount || 0 }}张照片</text>
          </view>
        </view>
      </view>

      <view v-if="loading" class="loading-tip"><text>加载中...</text></view>
      <view v-if="!loading && clusters.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F464;</text>
        <text class="empty-text">还没有识别人物</text>
        <text class="empty-sub">上传含人脸的照片后自动聚类</text>
      </view>

      <!-- 选中人物的照片时间线 -->
      <view v-if="selectedCluster" class="timeline-section">
        <view class="section-header">
          <text class="section-title">{{ selectedName }} 的时光</text>
          <text class="rename-btn" @click="renameCluster">重命名</text>
        </view>
        <view class="timeline-grid">
          <view
            v-for="photo in clusterPhotos"
            :key="photo.id"
            class="timeline-photo"
            @click="goDetail(photo.id)"
          >
            <image :src="photo.thumbUrl || '/static/placeholder.png'" mode="aspectFill" class="tl-img" />
            <text v-if="photo.shootTime" class="tl-time">{{ formatTime(photo.shootTime) }}</text>
          </view>
        </view>
        <view v-if="clusterPhotos.length === 0" class="empty-hint">
          <text>该人物暂无照片</text>
        </view>
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

interface ClusterItem {
  id: number
  name: string
  coverUrl: string
  photoCount: number
}

interface PhotoItem {
  id: number
  thumbUrl: string
  shootTime: string
}

const clusters = ref<ClusterItem[]>([])
const loading = ref(false)
const selectedCluster = ref<number | null>(null)
const selectedName = ref('')
const clusterPhotos = ref<PhotoItem[]>([])

async function fetchClusters() {
  if (!userStore.familyId || loading.value) return
  loading.value = true
  try {
    const result = await request<any[]>({
      url: `/api/face/clusters?familyId=${userStore.familyId}`
    }) as any
    clusters.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载人物列表失败', e)
  } finally {
    loading.value = false
  }
}

async function selectCluster(cluster: ClusterItem) {
  if (selectedCluster.value === cluster.id) {
    selectedCluster.value = null
    selectedName.value = ''
    clusterPhotos.value = []
    return
  }
  selectedCluster.value = cluster.id
  selectedName.value = cluster.name || '未命名'
  try {
    const result = await request<any[]>({
      url: `/api/face/cluster/${cluster.id}/photos`
    }) as any
    clusterPhotos.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载人物照片失败', e)
  }
}

function renameCluster() {
  uni.showModal({
    title: '重命名',
    editable: true,
    placeholderText: '输入新名称',
    success: async (res) => {
      if (res.confirm && res.content?.trim()) {
        try {
          await request({
            url: `/api/face/cluster/${selectedCluster.value}/rename`,
            method: 'PUT',
            data: { name: res.content.trim() }
          })
          const cluster = clusters.value.find(c => c.id === selectedCluster.value)
          if (cluster) {
            cluster.name = res.content.trim()
            selectedName.value = res.content.trim()
          }
          uni.showToast({ title: '已重命名' })
        } catch (e) {
          console.error('重命名失败', e)
        }
      }
    }
  })
}

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`
}

function goDetail(photoId: number) {
  uni.navigateTo({ url: `/pages/photo/detail?id=${photoId}` })
}

onShow(() => {
  fetchClusters()
})
</script>

<style scoped>
.person-page {
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

.person-scroll {
  height: calc(100vh - 120rpx);
  padding: 20rpx;
}

.cluster-list {
  margin-bottom: 30rpx;
}

.cluster-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  border: 2rpx solid transparent;
}

.cluster-item.active {
  border-color: #4a90d9;
  background: #f0f7ff;
}

.cluster-cover {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.cluster-info {
  flex: 1;
}

.cluster-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 6rpx;
}

.cluster-count {
  font-size: 24rpx;
  color: #999;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.rename-btn {
  font-size: 24rpx;
  color: #4a90d9;
}

.timeline-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.timeline-photo {
  width: calc(33.33% - 8rpx);
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}

.tl-img {
  width: 100%;
  height: 200rpx;
}

.tl-time {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  text-align: center;
}

.empty-hint {
  text-align: center;
  padding: 40rpx;
  font-size: 24rpx;
  color: #ccc;
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
  margin-bottom: 8rpx;
}

.empty-sub {
  font-size: 24rpx;
  color: #ccc;
}
</style>
