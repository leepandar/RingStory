<template>
  <view class="detail-page">
    <!-- 大图预览 -->
    <swiper class="photo-swiper" :current="currentIndex" @change="onSwiperChange">
      <swiper-item v-for="photo in photoList" :key="photo.id">
        <image
          :src="photo.url"
          mode="aspectFit"
          class="full-photo"
          @click="previewImage(photo.url)"
        />
      </swiper-item>
    </swiper>

    <!-- 底部信息卡片 -->
    <view class="info-card">
      <!-- 照片信息 -->
      <view class="photo-meta">
        <view class="meta-row">
          <text class="meta-label">拍摄时间</text>
          <text class="meta-value">{{ currentPhoto.shootTime || '未知' }}</text>
        </view>
        <view v-if="currentPhoto.location" class="meta-row">
          <text class="meta-label">拍摄地点</text>
          <text class="meta-value">{{ currentPhoto.location }}</text>
        </view>
        <view v-if="currentPhoto.uploaderName" class="meta-row">
          <text class="meta-label">上传者</text>
          <view class="uploader-info">
            <image v-if="currentPhoto.uploaderAvatar" :src="currentPhoto.uploaderAvatar" class="mini-avatar" />
            <text class="meta-value">{{ currentPhoto.uploaderName }}</text>
          </view>
        </view>
      </view>

      <!-- 备注卡片 -->
      <view v-if="currentPhoto.remark" class="remark-card">
        <text class="remark-text">{{ currentPhoto.remark }}</text>
      </view>

      <!-- 操作栏 -->
      <view class="action-bar">
        <view class="action-item" @click="handleLike">
          <text class="action-icon" :class="{ liked: isLiked }">{{ isLiked ? '&#x2764;' : '&#x2661;' }}</text>
          <text class="action-count">{{ likeCount }}</text>
        </view>
        <view class="action-item" @click="showComments = !showComments">
          <text class="action-icon">&#x1F4AC;</text>
          <text class="action-count">{{ comments.length }}</text>
        </view>
        <view class="action-item" @click="handleCollect">
          <text class="action-icon">{{ collected ? '&#x2605;' : '&#x2606;' }}</text>
        </view>
      </view>

      <!-- 评论区 -->
      <view v-if="showComments" class="comment-section">
        <view class="comment-list">
          <view v-for="comment in comments" :key="comment.id" class="comment-item">
            <image :src="comment.authorAvatar || '/static/placeholder.png'" class="comment-avatar" />
            <view class="comment-body">
              <text class="comment-author">{{ comment.authorName }}</text>
              <text class="comment-content">{{ comment.content }}</text>
              <text class="comment-time">{{ comment.createTime }}</text>
            </view>
          </view>
          <view v-if="comments.length === 0" class="no-comment">
            <text>暂无评论</text>
          </view>
        </view>
        <view class="comment-input">
          <input
            v-model="commentText"
            placeholder="说点什么..."
            class="input"
            confirm-type="send"
            @confirm="submitComment"
          />
          <button class="btn-send" :disabled="!commentText.trim()" @click="submitComment">发送</button>
        </view>
      </view>
    </view>

    <!-- 关闭按钮 -->
    <view class="close-btn" @click="goBack">
      <text class="close-icon">&#x2715;</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { toggleLike, getComments, addComment } from '../../api/album'

const userStore = useUserStore()

interface PhotoData {
  id: number
  url: string
  shootTime: string
  location: string
  uploaderName: string
  uploaderAvatar: string
  remark: string
  likeCount: number
}

interface CommentItem {
  id: number
  authorName: string
  authorAvatar: string
  content: string
  createTime: string
}

const photoList = ref<PhotoData[]>([])
const currentIndex = ref(0)
const isLiked = ref(false)
const likeCount = ref(0)
const collected = ref(false)
const comments = ref<CommentItem[]>([])
const showComments = ref(false)
const commentText = ref('')

const currentPhoto = computed(() => photoList.value[currentIndex.value] || {} as PhotoData)

onLoad((query) => {
  const photoId = query?.id ? Number(query.id) : 0
  // 从页面参数加载照片数据
  const eventChannel = (getCurrentPages().pop() as any)?.eventChannel
  if (eventChannel) {
    eventChannel.on('photoData', (data: PhotoData) => {
      photoList.value = [data]
      likeCount.value = data.likeCount || 0
      loadComments(data.id)
    })
  } else if (photoId) {
    // 降级：直接通过 ID 加载
    loadPhotoDetail(photoId)
  }
})

async function loadPhotoDetail(photoId: number) {
  // TODO: 调用后端获取照片详情 API
  console.log('加载照片详情', photoId)
}

async function loadComments(photoId: number) {
  try {
    const result = await getComments(photoId) as any
    comments.value = Array.isArray(result) ? result : []
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

function onSwiperChange(e: any) {
  currentIndex.value = e.detail.current
  const photo = photoList.value[currentIndex.value]
  if (photo) {
    likeCount.value = photo.likeCount || 0
    loadComments(photo.id)
  }
}

function previewImage(url: string) {
  uni.previewImage({
    urls: photoList.value.map(p => p.url),
    current: url
  })
}

async function handleLike() {
  if (!userStore.userInfo) return
  try {
    await toggleLike(currentPhoto.value.id, userStore.userInfo.id)
    isLiked.value = !isLiked.value
    likeCount.value += isLiked.value ? 1 : -1
  } catch (e) {
    console.error('点赞失败', e)
  }
}

function handleCollect() {
  collected.value = !collected.value
  uni.showToast({ title: collected.value ? '已收藏' : '取消收藏', icon: 'none' })
}

async function submitComment() {
  if (!commentText.value.trim() || !userStore.userInfo) return
  try {
    await addComment(currentPhoto.value.id, {
      authorId: userStore.userInfo.id,
      content: commentText.value
    })
    commentText.value = ''
    // 重新加载评论
    loadComments(currentPhoto.value.id)
  } catch (e) {
    console.error('评论失败', e)
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped>
.detail-page {
  height: 100vh;
  background: #000;
  position: relative;
}

.photo-swiper {
  height: 60vh;
}

.full-photo {
  width: 100%;
  height: 100%;
}

.info-card {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 30rpx;
  max-height: 45vh;
  overflow-y: auto;
}

.photo-meta {
  margin-bottom: 20rpx;
}

.meta-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.meta-label {
  font-size: 24rpx;
  color: #999;
  width: 140rpx;
}

.meta-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.uploader-info {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.mini-avatar {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
}

.remark-card {
  background: #f8f8f8;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.remark-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}

.action-bar {
  display: flex;
  gap: 60rpx;
  padding: 20rpx 0;
  border-top: 1rpx solid #f0f0f0;
  border-bottom: 1rpx solid #f0f0f0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.action-icon {
  font-size: 36rpx;
}

.action-icon.liked {
  color: #ff4d4f;
}

.action-count {
  font-size: 24rpx;
  color: #666;
}

.comment-section {
  margin-top: 20rpx;
}

.comment-list {
  max-height: 300rpx;
  overflow-y: auto;
}

.comment-item {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.comment-avatar {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-author {
  font-size: 24rpx;
  color: #4a90d9;
  font-weight: 500;
}

.comment-content {
  font-size: 26rpx;
  color: #333;
  margin-top: 6rpx;
}

.comment-time {
  font-size: 20rpx;
  color: #ccc;
  margin-top: 6rpx;
}

.no-comment {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #ccc;
}

.comment-input {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 16rpx;
}

.input {
  flex: 1;
  height: 64rpx;
  background: #f5f5f5;
  border-radius: 32rpx;
  padding: 0 24rpx;
  font-size: 26rpx;
}

.btn-send {
  padding: 0 24rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 32rpx;
  background: #4a90d9;
  color: #fff;
  font-size: 26rpx;
  border: none;
}

.btn-send[disabled] {
  background: #ccc;
}

.close-btn {
  position: absolute;
  top: calc(var(--status-bar-height, 44px) + 20rpx);
  right: 30rpx;
  width: 60rpx;
  height: 60rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 28rpx;
  color: #fff;
}
</style>
