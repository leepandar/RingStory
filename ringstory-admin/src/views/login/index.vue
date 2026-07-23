<template>
  <div class="login-page">
    <!-- 左侧品牌区域 -->
    <div class="brand-panel">
      <!-- 年轮装饰背景 -->
      <div class="ring-bg">
        <div class="ring ring-1" />
        <div class="ring ring-2" />
        <div class="ring ring-3" />
        <div class="ring ring-4" />
        <div class="ring ring-5" />
        <div class="ring ring-6" />
      </div>

      <!-- 浮动光点 -->
      <div class="particles">
        <span v-for="i in 12" :key="i" class="particle" :style="particleStyle(i)" />
      </div>

      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-rings">
            <svg viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="40" cy="40" r="36" stroke="currentColor" stroke-width="1.5" opacity="0.3">
                <animateTransform attributeName="transform" type="rotate" from="0 40 40" to="360 40 40" dur="30s" repeatCount="indefinite" />
              </circle>
              <circle cx="40" cy="40" r="26" stroke="currentColor" stroke-width="1.5" opacity="0.5">
                <animateTransform attributeName="transform" type="rotate" from="360 40 40" to="0 40 40" dur="20s" repeatCount="indefinite" />
              </circle>
              <circle cx="40" cy="40" r="16" stroke="currentColor" stroke-width="2" opacity="0.7" />
              <circle cx="40" cy="40" r="5" fill="currentColor">
                <animate attributeName="r" values="5;6;5" dur="3s" repeatCount="indefinite" />
              </circle>
            </svg>
          </div>
          <span class="logo-text">RingStory</span>
        </div>

        <h1 class="brand-title">
          <span class="title-line">时光为轮</span>
          <span class="title-line accent">记忆成书</span>
        </h1>

        <p class="brand-desc">
        每一张照片都是一圈年轮，记录着家庭时光的温暖与美好。
        </p>

        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-icon">📷</div>
            <div class="feature-text">
              <span class="feature-label">智能相册</span>
              <span class="feature-desc">AI 分类整理</span>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🌳</div>
            <div class="feature-text">
              <span class="feature-label">年轮图谱</span>
              <span class="feature-desc">时光可视化</span>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🎬</div>
            <div class="feature-text">
              <span class="feature-label">年轮放映室</span>
              <span class="feature-desc">自动生成回顾</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="form-panel">
      <div class="form-wrapper">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录管理后台，管理家庭年轮内容</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent="onSubmit" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入管理员账号"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              class="custom-input"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="onSubmit"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 滑块验证码区域 -->
        <Transition name="fade-slide">
          <div v-if="showCaptcha" class="captcha-area">
            <SliderCaptcha ref="captchaRef" @success="onCaptchaSuccess" @fail="onCaptchaFail" />
          </div>
        </Transition>

        <div class="form-footer">
          <span class="footer-text">RingStory Admin v1.0</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const captchaRef = ref<InstanceType<typeof SliderCaptcha>>()
const loading = ref(false)
const showCaptcha = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function particleStyle(i: number) {
  const size = 3 + Math.random() * 5
  const x = Math.random() * 100
  const y = Math.random() * 100
  const delay = Math.random() * 8
  const duration = 6 + Math.random() * 8
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${x}%`,
    top: `${y}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}

async function onSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  showCaptcha.value = true
}

async function onCaptchaSuccess(verifyToken: string) {
  loading.value = true
  try {
    const success = await authStore.login(form.username, form.password, verifyToken)
    if (success) {
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error('登录失败，请检查账号密码')
      captchaRef.value?.refresh()
    }
  } finally {
    loading.value = false
  }
}

function onCaptchaFail() {
  // 组件内部自动刷新验证码
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  background: #f0f2f5;
}

/* ==================== 左侧品牌面板 ==================== */
.brand-panel {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #0d1117 0%, #161b22 40%, #1a2332 70%, #0d1117 100%);
  overflow: hidden;
}

/* 年轮背景装饰 */
.ring-bg {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(88, 166, 255, 0.06);
}

.ring-1 { width: 200px; height: 200px; animation: ringPulse 8s ease-in-out infinite; }
.ring-2 { width: 320px; height: 320px; animation: ringPulse 8s ease-in-out infinite 0.5s; }
.ring-3 { width: 440px; height: 440px; animation: ringPulse 8s ease-in-out infinite 1s; }
.ring-4 { width: 560px; height: 560px; animation: ringPulse 8s ease-in-out infinite 1.5s; }
.ring-5 { width: 680px; height: 680px; animation: ringPulse 8s ease-in-out infinite 2s; }
.ring-6 { width: 800px; height: 800px; animation: ringPulse 8s ease-in-out infinite 2.5s; }

@keyframes ringPulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.03); }
}

/* 浮动光点 */
.particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(88, 166, 255, 0.4);
  animation: particleFloat 8s ease-in-out infinite;
}

@keyframes particleFloat {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.3; }
  50% { transform: translateY(-30px) scale(1.5); opacity: 0.8; }
}

.brand-content {
  position: relative;
  z-index: 2;
  padding: 48px;
  max-width: 500px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 48px;
}

.logo-rings {
  width: 56px;
  height: 56px;
  color: #58a6ff;
}

.logo-rings svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  color: #e6edf3;
  letter-spacing: 1px;
}

.brand-title {
  margin: 0 0 24px;
}

.title-line {
  display: block;
  font-size: 44px;
  font-weight: 800;
  color: #e6edf3;
  line-height: 1.3;
  letter-spacing: 2px;
}

.title-line.accent {
  background: linear-gradient(135deg, #58a6ff, #a371f7, #f778ba);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-desc {
  font-size: 15px;
  color: rgba(230, 237, 243, 0.5);
  line-height: 1.9;
  margin: 0 0 48px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  backdrop-filter: blur(8px);
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(88, 166, 255, 0.2);
  transform: translateX(4px);
}

.feature-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(88, 166, 255, 0.1);
  border-radius: 10px;
  flex-shrink: 0;
}

.feature-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.feature-label {
  font-size: 14px;
  font-weight: 600;
  color: #e6edf3;
}

.feature-desc {
  font-size: 12px;
  color: rgba(230, 237, 243, 0.4);
}

/* ==================== 右侧表单面板 ==================== */
.form-panel {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  box-shadow: -4px 0 24px rgba(0, 0, 0, 0.06);
}

.form-wrapper {
  width: 100%;
  max-width: 380px;
  padding: 40px 0;
}

.form-header {
  margin-bottom: 40px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 10px;
}

.form-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

/* ==================== 表单样式 ==================== */
.login-form {
  margin-bottom: 8px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 16px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #b0b8c4 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1a1a2e inset;
}

.login-form :deep(.el-input__prefix .el-icon) {
  font-size: 18px;
  color: #8c8c8c;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(26, 26, 46, 0.35);
}

.login-btn:active {
  transform: translateY(0);
}

/* ==================== 验证码区域 ==================== */
.captcha-area {
  display: flex;
  justify-content: center;
  margin: 16px 0 0;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(-12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

/* ==================== 底部 ==================== */
.form-footer {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.footer-text {
  font-size: 12px;
  color: #bbb;
}

/* ==================== 响应式 ==================== */
@media (max-width: 900px) {
  .brand-panel {
    display: none;
  }

  .form-panel {
    width: 100%;
  }

  .login-page {
    background: #ffffff;
  }
}
</style>
