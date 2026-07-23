<template>
  <view class="search-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">&#x1F50D;</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索照片、备注、地点..."
          confirm-type="search"
          @confirm="doSearch"
        />
        <text v-if="keyword" class="clear-btn" @click="clearKeyword">&#x2715;</text>
      </view>
      <text class="cancel-btn" @click="goBack">取消</text>
    </view>

    <!-- 搜索历史 -->
    <view v-if="!hasSearched" class="history-section">
      <view class="section-header">
        <text class="section-title">搜索历史</text>
        <text v-if="history.length > 0" class="clear-history" @click="clearHistory">清空</text>
      </view>
      <view class="history-tags">
        <text
          v-for="(item, idx) in history"
          :key="idx"
          class="history-tag"
          @click="searchByHistory(item)"
        >{{ item }}</text>
      </view>
      <view v-if="history.length === 0" class="empty-history">
        <text>暂无搜索记录</text>
      </view>
    </view>

    <!-- 搜索结果 -->
    <scroll-view v-else scroll-y class="result-scroll" @scrolltolower="loadMore">
      <!-- 筛选栏 -->
      <view class="filter-bar">
        <text
          class="filter-item"
          :class="{ active: filterType === 'all' }"
          @click="changeFilter('all')"
        >全部</text>
        <text
          class="filter-item"
          :class="{ active: filterType === 'note' }"
          @click="changeFilter('note')"
        >备注</text>
        <text
          class="filter-item"
          :class="{ active: filterType === 'tag' }"
          @click="changeFilter('tag')"
        >标签</text>
        <text
          class="filter-item"
          :class="{ active: filterType === 'location' }"
          @click="changeFilter('location')"
        >地点</text>
      </view>

      <view
        v-for="photo in results"
        :key="photo.id"
        class="result-item"
        @click="goDetail(photo.id)"
      >
        <image :src="photo.thumbUrl || '/static/placeholder.png'" mode="aspectFill" class="result-img" />
        <view class="result-info">
          <text class="result-note">{{ photo.note || '无备注' }}</text>
          <text class="result-meta">{{ photo.shootTime || '' }} {{ photo.location ? '· ' + photo.location : '' }}</text>
          <view v-if="photo.tags && photo.tags.length" class="result-tags">
            <text v-for="tag in photo.tags.slice(0, 3)" :key="tag" class="result-tag">{{ tag }}</text>
          </view>
        </view>
      </view>

      <view v-if="loading" class="loading-tip"><text>搜索中...</text></view>
      <view v-if="!loading && results.length === 0 && hasSearched" class="empty-state">
        <text class="empty-icon">&#x1F50D;</text>
        <text class="empty-text">未找到相关内容</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '../../stores/user'
import { request } from '../../utils/request'

const userStore = useUserStore()

interface SearchResult {
  id: number
  thumbUrl: string
  note: string
  shootTime: string
  location: string
  tags: string[]
}

const keyword = ref('')
const history = ref<string[]>([])
const hasSearched = ref(false)
const results = ref<SearchResult[]>([])
const loading = ref(false)
const page = ref(1)
const noMore = ref(false)
const filterType = ref('all')

onLoad(() => {
  loadHistory()
})

function loadHistory() {
  try {
    const stored = uni.getStorageSync('search_history')
    if (stored) {
      history.value = JSON.parse(stored)
    }
  } catch (_e) { /* ignore */ }
}

function saveHistory(word: string) {
  const list = history.value.filter(h => h !== word)
  list.unshift(word)
  if (list.length > 10) list.pop()
  history.value = list
  uni.setStorageSync('search_history', JSON.stringify(list))
}

function clearHistory() {
  history.value = []
  uni.removeStorageSync('search_history')
}

function searchByHistory(word: string) {
  keyword.value = word
  doSearch()
}

function clearKeyword() {
  keyword.value = ''
  hasSearched.value = false
  results.value = []
}

async function doSearch() {
  const word = keyword.value.trim()
  if (!word) return
  saveHistory(word)
  hasSearched.value = true
  page.value = 1
  noMore.value = false
  results.value = []
  await fetchResults()
}

async function fetchResults() {
  if (loading.value) return
  loading.value = true
  try {
    const params = new URLSearchParams({
      keyword: keyword.value.trim(),
      familyId: String(userStore.familyId || ''),
      page: String(page.value),
      size: '20'
    })
    if (filterType.value !== 'all') {
      params.set('type', filterType.value)
    }
    const result = await request<any>({
      url: `/api/search/photos?${params.toString()}`
    }) as any
    const list: SearchResult[] = Array.isArray(result) ? result : (result?.records || [])
    if (list.length < 20) noMore.value = true
    if (page.value === 1) {
      results.value = list
    } else {
      results.value.push(...list)
    }
    page.value++
  } catch (e) {
    console.error('搜索失败', e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) fetchResults()
}

function changeFilter(type: string) {
  filterType.value = type
  page.value = 1
  noMore.value = false
  fetchResults()
}

function goDetail(photoId: number) {
  uni.navigateTo({ url: `/pages/photo/detail?id=${photoId}` })
}

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 20rpx);
  background: #fff;
}

.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f0f0f0;
  border-radius: 36rpx;
  padding: 0 20rpx;
  height: 64rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  height: 64rpx;
}

.clear-btn {
  font-size: 24rpx;
  color: #999;
  padding: 10rpx;
}

.cancel-btn {
  font-size: 28rpx;
  color: #4a90d9;
  margin-left: 20rpx;
}

.history-section {
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.clear-history {
  font-size: 24rpx;
  color: #999;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.history-tag {
  background: #fff;
  padding: 10rpx 24rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  color: #666;
}

.empty-history {
  font-size: 24rpx;
  color: #ccc;
  padding: 20rpx 0;
}

.filter-bar {
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-item {
  font-size: 26rpx;
  color: #666;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
}

.filter-item.active {
  background: #4a90d9;
  color: #fff;
}

.result-scroll {
  height: calc(100vh - 200rpx);
}

.result-item {
  display: flex;
  padding: 20rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.result-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.result-note {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.result-meta {
  font-size: 22rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.result-tags {
  display: flex;
  gap: 10rpx;
}

.result-tag {
  font-size: 20rpx;
  background: #e6f7ff;
  color: #4a90d9;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
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
