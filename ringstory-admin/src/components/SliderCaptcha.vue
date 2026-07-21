<template>
  <div class="slider-captcha">
    <!-- 图片区域 -->
    <div class="captcha-image-area" :style="{ width: imageWidth + 'px', height: imageHeight + 'px' }">
      <!-- 背景图（带镂空） -->
      <img v-if="bgImage" :src="bgImage" class="bg-image" draggable="false" />
      <!-- 拼图块 -->
      <img
        v-if="pieceImage"
        :src="pieceImage"
        class="piece-image"
        :style="{ top: pieceY + 'px', left: currentX + 'px' }"
        draggable="false"
      />
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-mask">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <!-- 结果提示 -->
      <div v-if="resultState === 'success'" class="result-mask success">
        <el-icon :size="32"><CircleCheck /></el-icon>
        <span>验证成功</span>
      </div>
      <div v-if="resultState === 'fail'" class="result-mask fail">
        <el-icon :size="32"><CircleClose /></el-icon>
        <span>验证失败，请重试</span>
      </div>
    </div>

    <!-- 滑块区域 -->
    <div class="slider-track" :style="{ width: imageWidth + 'px' }">
      <div class="slider-fill" :style="{ width: currentX + 'px' }" />
      <div
        class="slider-handle"
        :class="{ active: isDragging, disabled: resultState === 'success' }"
        :style="{ left: currentX + 'px' }"
        @mousedown.prevent="onDragStart"
        @touchstart.prevent="onDragStart"
      >
        <span class="handle-icon">&rArr;</span>
      </div>
      <div v-if="!isDragging && currentX === 0 && resultState === ''" class="slider-tip">
        向右拖动滑块完成验证
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Loading, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import http from '@/utils/request'

const emit = defineEmits<{
  (e: 'success', verifyToken: string): void
  (e: 'fail'): void
  (e: 'refresh'): void
}>()

const imageWidth = 320
const imageHeight = 160

const bgImage = ref('')
const pieceImage = ref('')
const pieceY = ref(0)
const captchaId = ref('')
const loading = ref(false)
const isDragging = ref(false)
const startX = ref(0)
const currentX = ref(0)
const resultState = ref<'' | 'success' | 'fail'>('')

async function fetchCaptcha() {
  loading.value = true
  resultState.value = ''
  currentX.value = 0
  try {
    const data = await http.get<any, any>('/user/captcha/generate')
    bgImage.value = data.backgroundImage
    pieceImage.value = data.pieceImage
    pieceY.value = data.y
    captchaId.value = data.captchaId
  } catch (e) {
    console.error('获取验证码失败', e)
  } finally {
    loading.value = false
  }
}

function onDragStart(e: MouseEvent | TouchEvent) {
  if (resultState.value === 'success' || loading.value) return
  isDragging.value = true
  startX.value = (e instanceof MouseEvent ? e.clientX : e.touches[0].clientX) - currentX.value

  document.addEventListener('mousemove', onDragMove)
  document.addEventListener('mouseup', onDragEnd)
  document.addEventListener('touchmove', onDragMove)
  document.addEventListener('touchend', onDragEnd)
}

function onDragMove(e: MouseEvent | TouchEvent) {
  if (!isDragging.value) return
  const clientX = e instanceof MouseEvent ? e.clientX : e.touches[0].clientX
  let newX = clientX - startX.value
  // 限制范围
  newX = Math.max(0, Math.min(newX, imageWidth - 44))
  currentX.value = newX
}

function onDragEnd() {
  if (!isDragging.value) return
  isDragging.value = false

  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
  document.removeEventListener('touchmove', onDragMove)
  document.removeEventListener('touchend', onDragEnd)

  if (currentX.value < 5) {
    // 没有实质性拖动，忽略
    return
  }

  verifySlide()
}

async function verifySlide() {
  loading.value = true
  try {
    const data = await http.post<any, any>('/user/captcha/verify', {
      captchaId: captchaId.value,
      x: Math.round(currentX.value)
    })
    if (data.success) {
      resultState.value = 'success'
      emit('success', data.verifyToken)
    } else {
      resultState.value = 'fail'
      emit('fail')
      // 2秒后刷新验证码
      setTimeout(() => {
        fetchCaptcha()
      }, 1500)
    }
  } catch (e) {
    resultState.value = 'fail'
    setTimeout(() => {
      fetchCaptcha()
    }, 1500)
  } finally {
    loading.value = false
  }
}

function refresh() {
  fetchCaptcha()
}

onMounted(() => {
  fetchCaptcha()
})

defineExpose({ refresh })
</script>

<style scoped>
.slider-captcha {
  display: inline-block;
  user-select: none;
}

.captcha-image-area {
  position: relative;
  overflow: hidden;
  border-radius: 4px;
  background: #e8e8e8;
}

.bg-image {
  width: 100%;
  height: 100%;
  display: block;
}

.piece-image {
  position: absolute;
  z-index: 2;
}

.loading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 14px;
  z-index: 10;
}

.result-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  z-index: 10;
}

.result-mask.success {
  background: rgba(82, 196, 26, 0.6);
  color: #fff;
}

.result-mask.fail {
  background: rgba(255, 77, 79, 0.5);
  color: #fff;
}

.slider-track {
  position: relative;
  height: 40px;
  margin-top: 10px;
  background: #f5f5f5;
  border-radius: 20px;
  border: 1px solid #e4e7eb;
  overflow: hidden;
}

.slider-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #d9ecff;
  border-radius: 20px 0 0 20px;
  transition: none;
}

.slider-handle {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 36px;
  background: #409eff;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  z-index: 5;
  transition: background 0.2s;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
}

.slider-handle:hover,
.slider-handle.active {
  background: #337ecc;
  cursor: grabbing;
}

.slider-handle.disabled {
  background: #67c23a;
  cursor: default;
}

.handle-icon {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.slider-tip {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #999;
  pointer-events: none;
}
</style>
