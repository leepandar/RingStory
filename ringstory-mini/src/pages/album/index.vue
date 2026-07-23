<template>
  <view class="album-page">
    <view class="top-bar">
      <text class="bar-title">影集</text>
      <text class="bar-action" @click="showCreate = true">+ 新建</text>
    </view>

    <scroll-view scroll-y class="album-scroll">
      <view
        v-for="album in albums"
        :key="album.id"
        class="album-card"
        @click="openAlbum(album)"
      >
        <image
          :src="album.coverUrl || '/static/placeholder.png'"
          mode="aspectFill"
          class="album-cover"
        />
        <view class="album-info">
          <text class="album-name">{{ album.name }}</text>
          <text class="album-desc">{{ album.description || '暂无描述' }}</text>
          <text class="album-count">{{ album.photoCount || 0 }}张照片</text>
        </view>
      </view>

      <view v-if="loading" class="loading-tip"><text>加载中...</text></view>
      <view v-if="!loading && albums.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F4D4;</text>
        <text class="empty-text">还没有影集</text>
        <text class="empty-sub">点击右上角新建影集</text>
      </view>
    </scroll-view>

    <!-- 新建影集弹窗 -->
    <view v-if="showCreate" class="modal-mask" @click.stop="showCreate = false">
      <view class="modal-content" @click.stop>
        <text class="modal-title">新建影集</text>
        <input class="modal-input" v-model="newAlbum.name" placeholder="影集名称" />
        <textarea class="modal-textarea" v-model="newAlbum.description" placeholder="描述（选填）" />
        <view class="modal-btns">
          <button class="modal-cancel" @click="showCreate = false">取消</button>
          <button class="modal-confirm" @click="createAlbum">创建</button>
        </view>
      </view>
    </view>

    <!-- 影集详情弹窗 -->
    <view v-if="showDetail" class="modal-mask" @click.stop="showDetail = false">
      <view class="modal-content detail-modal" @click.stop>
        <text class="modal-title">{{ currentAlbum?.name }}</text>
        <scroll-view scroll-y class="detail-photos">
          <view
            v-for="photo in detailPhotos"
            :key="photo.id"
            class="detail-photo"
            @click="goDetail(photo.id)"
          >
            <image :src="photo.thumbUrl" mode="aspectFill" class="detail-img" />
          </view>
        </scroll-view>
        <view class="modal-btns">
          <button class="modal-cancel" @click="showDetail = false">关闭</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { request } from '../../utils/request'

const userStore = useUserStore()

interface AlbumItem {
  id: number
  name: string
  description: string
  coverUrl: string
  photoCount: number
}

interface PhotoItem {
  id: number
  thumbUrl: string
}

const albums = ref<AlbumItem[]>([])
const loading = ref(false)
const showCreate = ref(false)
const showDetail = ref(false)
const currentAlbum = ref<AlbumItem | null>(null)
const detailPhotos = ref<PhotoItem[]>([])
const newAlbum = reactive({ name: '', description: '' })

async function fetchAlbums() {
  if (!userStore.familyId || loading.value) return
  loading.value = true
  try {
    const result = await request<any[]>({
      url: `/api/album/list?familyId=${userStore.familyId}`
    }) as any
    albums.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载影集失败', e)
  } finally {
    loading.value = false
  }
}

async function createAlbum() {
  if (!newAlbum.name.trim()) {
    uni.showToast({ title: '请输入影集名称', icon: 'none' })
    return
  }
  try {
    await request({
      url: '/api/album/create',
      method: 'POST',
      data: {
        familyId: userStore.familyId,
        name: newAlbum.name.trim(),
        description: newAlbum.description.trim()
      }
    })
    showCreate.value = false
    newAlbum.name = ''
    newAlbum.description = ''
    fetchAlbums()
    uni.showToast({ title: '创建成功' })
  } catch (e) {
    console.error('创建影集失败', e)
  }
}

async function openAlbum(album: AlbumItem) {
  currentAlbum.value = album
  try {
    const result = await request<any[]>({
      url: `/api/album/${album.id}/photos`
    }) as any
    detailPhotos.value = Array.isArray(result) ? result : []
    showDetail.value = true
  } catch (e) {
    console.error('加载影集照片失败', e)
  }
}

function goDetail(photoId: number) {
  uni.navigateTo({ url: `/pages/photo/detail?id=${photoId}` })
}

onShow(() => {
  fetchAlbums()
})
</script>

<style scoped>
.album-page {
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
  font-size: 28rpx;
  color: #4a90d9;
}

.album-scroll {
  height: calc(100vh - 120rpx);
  padding: 20rpx;
}

.album-card {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.album-cover {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
}

.album-info {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.album-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.album-desc {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.album-count {
  font-size: 22rpx;
  color: #ccc;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  width: 80%;
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 30rpx;
  text-align: center;
}

.modal-input {
  background: #f5f5f5;
  border-radius: 8rpx;
  padding: 20rpx;
  font-size: 28rpx;
  margin-bottom: 20rpx;
}

.modal-textarea {
  background: #f5f5f5;
  border-radius: 8rpx;
  padding: 20rpx;
  font-size: 28rpx;
  margin-bottom: 30rpx;
  height: 120rpx;
  width: 100%;
}

.modal-btns {
  display: flex;
  gap: 20rpx;
}

.modal-cancel,
.modal-confirm {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  border-radius: 36rpx;
  font-size: 28rpx;
  text-align: center;
}

.modal-cancel {
  background: #f0f0f0;
  color: #666;
  border: none;
}

.modal-confirm {
  background: #4a90d9;
  color: #fff;
  border: none;
}

.detail-modal {
  max-height: 70vh;
}

.detail-photos {
  max-height: 50vh;
}

.detail-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.detail-photo {
  width: calc(33.33% - 8rpx);
  border-radius: 8rpx;
  overflow: hidden;
}

.detail-img {
  width: 100%;
  height: 180rpx;
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
