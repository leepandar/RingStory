<template>
  <view class="tag-page">
    <view class="top-bar">
      <text class="bar-title">标签管理</text>
      <text class="bar-action" @click="showCreate = true">+ 新建</text>
    </view>

    <scroll-view scroll-y class="tag-scroll">
      <!-- 标签列表 -->
      <view class="tag-grid">
        <view
          v-for="tag in tags"
          :key="tag.id"
          class="tag-item"
          :class="{ active: selectedTag === tag.id }"
          @click="selectTag(tag)"
        >
          <text class="tag-name">{{ tag.name }}</text>
          <text class="tag-count">{{ tag.photoCount || 0 }}</text>
        </view>
      </view>

      <view v-if="loading" class="loading-tip"><text>加载中...</text></view>
      <view v-if="!loading && tags.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F3F7;</text>
        <text class="empty-text">还没有标签</text>
      </view>

      <!-- 选中标签的照片列表 -->
      <view v-if="selectedTag" class="tag-photos">
        <view class="section-title">
          <text>{{ selectedTagName }} 的照片</text>
        </view>
        <view class="photo-grid">
          <view
            v-for="photo in tagPhotos"
            :key="photo.id"
            class="photo-item"
            @click="goDetail(photo.id)"
          >
            <image :src="photo.thumbUrl || '/static/placeholder.png'" mode="aspectFill" class="photo-img" />
          </view>
        </view>
        <view v-if="tagPhotos.length === 0" class="empty-hint">
          <text>该标签下暂无照片</text>
        </view>
      </view>
    </scroll-view>

    <!-- 新建标签弹窗 -->
    <view v-if="showCreate" class="modal-mask" @click.stop="showCreate = false">
      <view class="modal-content" @click.stop>
        <text class="modal-title">新建标签</text>
        <input class="modal-input" v-model="newTagName" placeholder="标签名称" @confirm="createTag" />
        <view class="modal-btns">
          <button class="modal-cancel" @click="showCreate = false">取消</button>
          <button class="modal-confirm" @click="createTag">创建</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { request } from '../../utils/request'

const userStore = useUserStore()

interface TagItem {
  id: number
  name: string
  photoCount: number
}

interface PhotoItem {
  id: number
  thumbUrl: string
}

const tags = ref<TagItem[]>([])
const loading = ref(false)
const showCreate = ref(false)
const newTagName = ref('')
const selectedTag = ref<number | null>(null)
const selectedTagName = ref('')
const tagPhotos = ref<PhotoItem[]>([])

async function fetchTags() {
  if (!userStore.familyId || loading.value) return
  loading.value = true
  try {
    const result = await request<any[]>({
      url: `/api/tag/list?familyId=${userStore.familyId}`
    }) as any
    tags.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载标签失败', e)
  } finally {
    loading.value = false
  }
}

async function createTag() {
  if (!newTagName.value.trim()) {
    uni.showToast({ title: '请输入标签名称', icon: 'none' })
    return
  }
  try {
    await request({
      url: '/api/tag/create',
      method: 'POST',
      data: {
        familyId: userStore.familyId,
        name: newTagName.value.trim()
      }
    })
    showCreate.value = false
    newTagName.value = ''
    fetchTags()
    uni.showToast({ title: '创建成功' })
  } catch (e) {
    console.error('创建标签失败', e)
  }
}

async function selectTag(tag: TagItem) {
  if (selectedTag.value === tag.id) {
    selectedTag.value = null
    selectedTagName.value = ''
    tagPhotos.value = []
    return
  }
  selectedTag.value = tag.id
  selectedTagName.value = tag.name
  try {
    const result = await request<any[]>({
      url: `/api/tag/${tag.id}/photos?familyId=${userStore.familyId}`
    }) as any
    tagPhotos.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载标签照片失败', e)
  }
}

function goDetail(photoId: number) {
  uni.navigateTo({ url: `/pages/photo/detail?id=${photoId}` })
}

onShow(() => {
  fetchTags()
})
</script>

<style scoped>
.tag-page {
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

.tag-scroll {
  height: calc(100vh - 120rpx);
  padding: 20rpx;
}

.tag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 30rpx;
}

.tag-item {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 14rpx 24rpx;
  border-radius: 24rpx;
  border: 2rpx solid #e0e0e0;
}

.tag-item.active {
  background: #e6f7ff;
  border-color: #4a90d9;
}

.tag-name {
  font-size: 26rpx;
  color: #333;
  margin-right: 10rpx;
}

.tag-count {
  font-size: 22rpx;
  color: #999;
  background: #f0f0f0;
  padding: 2rpx 10rpx;
  border-radius: 12rpx;
}

.tag-item.active .tag-count {
  background: #4a90d9;
  color: #fff;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.photo-item {
  width: calc(33.33% - 8rpx);
  border-radius: 12rpx;
  overflow: hidden;
}

.photo-img {
  width: 100%;
  height: 200rpx;
}

.empty-hint {
  text-align: center;
  padding: 40rpx;
  font-size: 24rpx;
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
  margin-bottom: 30rpx;
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
