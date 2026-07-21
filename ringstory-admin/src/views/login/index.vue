<template>
  <div class="login-page">
    <!-- 左侧品牌区域 -->
    <div class="brand-panel">
      <!-- 动态背景装饰 -->
      <div class="bg-decoration">
        <div class="circle circle-1" />
        <div class="circle circle-2" />
        <div class="circle circle-3" />
        <div class="circle circle-4" />
      </div>

      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="24" cy="24" r="20" stroke="currentColor" stroke-width="2.5" />
              <circle cx="24" cy="24" r="13" stroke="currentColor" stroke-width="2" opacity="0.7" />
              <circle cx="24" cy="24" r="6" fill="currentColor" opacity="0.9" />
            </svg>
          </div>
          <span class="logo-text">RingStory</span>
        </div>
        <h1 class="brand-title">家庭时光年轮</h1>
        <p class="brand-desc">
          时光为轮，<br />
          记忆成书。
        </p>
        <div class="brand-stats">
          <div class="stat-item">
            <span class="stat-number">128+</span>
            <span class="stat-label">活跃家庭</span>
          </div>
          <div class="stat-divider" />
          <div class="stat-item">
            <span class="stat-number">3,600+</span>
            <span class="stat-label">珍贵照片</span>
          </div>
          <div class="stat-divider" />
          <div class="stat-item">
            <span class="stat-number">99.9%</span>
            <span class="stat-label">稳定运行</span>
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
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.circle-1 {
  width: 500px;
  height: 500px;
  background: #e94560;
  top: -120px;
  left: -100px;
  animation: float 20s ease-in-out infinite;
}

.circle-2 {
  width: 350px;
  height: 350px;
  background: #0f3460;
  bottom: -80px;
  right: -60px;
  animation: float 15s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: #533483;
  top: 40%;
  right: 20%;
  animation: float 18s ease-in-out infinite 2s;
}

.circle-4 {
  width: 120px;
  height: 120px;
  background: #e94560;
  bottom: 25%;
  left: 15%;
  animation: float 12s ease-in-out infinite 1s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -30px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(15px, 15px) scale(1.02); }
}

.brand-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 40px;
  max-width: 480px;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-bottom: 40px;
}

.logo-icon {
  width: 52px;
  height: 52px;
  color: #e94560;
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 2px;
}

.brand-title {
  font-size: 42px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 20px;
  line-height: 1.3;
}

.brand-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.8;
  margin: 0 0 50px;
}

.brand-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 28px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #e94560;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.stat-divider {
  width: 1px;
  height: 36px;
  background: rgba(255, 255, 255, 0.15);
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
