<script setup lang="ts">
import { onLaunch, onShow } from '@dcloudio/uni-app'
import { useUserStore } from './stores/user'

onLaunch(() => {
  console.log('RingStory App Launch')
  const userStore = useUserStore()
  userStore.init()

  // 判断是否首次使用
  const onboarded = uni.getStorageSync('onboarded')
  if (!onboarded) {
    uni.navigateTo({ url: '/pages/onboarding/index' })
    return
  }

  // 已登录且有家庭，直接进首页（pages.json 第一个页面即首页）
  // 未登录则停留在登录页
})

onShow(() => {
  console.log('App Show')
})
</script>

<style>
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
