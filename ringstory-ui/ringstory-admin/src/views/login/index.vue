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
      </div>

      <!-- 浮动光点 -->
      <div class="particles">
        <span v-for="i in 8" :key="i" class="particle dot-fx" :class="'dot-' + i" />
      </div>

      <div class="brand-content">
        <!-- 三层同心圆 Logo -->
        <div class="brand-logo">
          <div class="logo-rings">
            <span class="logo-icon">🌳</span>
          </div>
        </div>
        <span class="logo-text">RingStory</span>

        <h1 class="brand-title">
          <span class="title-line">时光为轮</span>
          <span class="title-line accent">记忆成书</span>
        </h1>

        <p class="brand-desc">
        每一张照片都是一圈年轮，记录着家庭时光的温暖与美好。
        </p>

        <!-- 玻璃态功能亮点栏 -->
        <div class="highlights">
          <div class="highlight-item">
            <span class="hl-icon">📷</span>
            <span class="hl-text">智能整理</span>
          </div>
          <div class="hl-divider" />
          <div class="highlight-item">
            <span class="hl-icon">🌳</span>
            <span class="hl-text">年轮图谱</span>
          </div>
          <div class="hl-divider" />
          <div class="highlight-item">
            <span class="hl-icon">🎬</span>
            <span class="hl-text">时光放映</span>
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
  border: 1px solid rgba(88, 166, 255, 0.08);
  left: 50%;
  top: 45%;
  transform: translate(-50%, -50%);
}

.ring-1 { width: 200px; height: 200px; animation: ringBreath 6s ease-in-out infinite; }
.ring-2 { width: 320px; height: 320px; animation: ringBreath 6s ease-in-out infinite 0.5s; }
.ring-3 { width: 440px; height: 440px; animation: ringBreath 6s ease-in-out infinite 1s; }
.ring-4 { width: 560px; height: 560px; animation: ringBreath 6s ease-in-out infinite 1.5s; }

@keyframes ringBreath {
  0%, 100% { opacity: 0.3; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 1; transform: translate(-50%, -50%) scale(1.02); }
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
  background: rgba(88, 166, 255, 0.35);
  animation: dotFloat 7s ease-in-out infinite;
}

.dot-1 { width: 6px; height: 6px; left: 15%; top: 20%; animation-delay: 0s; }
.dot-2 { width: 4px; height: 4px; left: 80%; top: 15%; animation-delay: 1s; }
.dot-3 { width: 8px; height: 8px; left: 70%; top: 60%; animation-delay: 2s; }
.dot-4 { width: 4px; height: 4px; left: 25%; top: 70%; animation-delay: 3s; }
.dot-5 { width: 5px; height: 5px; left: 60%; top: 35%; animation-delay: 0.5s; }
.dot-6 { width: 3px; height: 3px; left: 40%; top: 80%; animation-delay: 1.5s; }
.dot-7 { width: 7px; height: 7px; left: 85%; top: 45%; animation-delay: 2.5s; }
.dot-8 { width: 5px; height: 5px; left: 10%; top: 50%; animation-delay: 4s; }

@keyframes dotFloat {
  0%, 100% { transform: translateY(0); opacity: 0.2; }
  50% { transform: translateY(-15px); opacity: 0.7; }
}

.brand-content {
  position: relative;
  z-index: 2;
  padding: 48px;
  max-width: 500px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-logo {
  margin-bottom: 48px;
}

/* 三层同心圆 —— radial-gradient 硬停点方案 */
.logo-rings {
  width: 240px;
  height: 240px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle,
      /* 中心渐变圆 */
      #00d4ff 0%,
      #a371f7 18%,
      /* 内圆边缘到中环之间 (透明间隙) */
      transparent 19%,
      transparent 37%,
      /* 中环 - 白色粗环带 */
      #ffffff 38%,
      #ffffff 46%,
      /* 中环到外环之间 (透明间隙) */
      transparent 47%,
      transparent 66%,
      /* 外环 - 白色粗环带 */
      #ffffff 67%,
      #ffffff 76%,
      /* 外环之外透明 */
      transparent 77%,
      transparent 100%
    );
  box-shadow: 0 0 40px 8px rgba(0, 212, 255, 0.35), 0 0 80px 4px rgba(163, 113, 247, 0.15);
}

.logo-icon {
  font-size: 40px;
  filter: drop-shadow(0 0 8px rgba(255,255,255,0.6));
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

/* 玻璃态功能亮点栏 */
.highlights {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 40px;
  backdrop-filter: blur(10px);
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.hl-icon {
  font-size: 18px;
}

.hl-text {
  font-size: 13px;
  color: rgba(230, 237, 243, 0.7);
}

.hl-divider {
  width: 1px;
  height: 18px;
  background: rgba(255, 255, 255, 0.12);
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
