<template>
  <div class="settings-page">
    <el-card>
      <template #header><span>通知模板配置</span></template>
      <el-form label-width="120px">
        <el-form-item label="新照片通知">
          <el-switch v-model="settings.newPhotoNotify" />
        </el-form-item>
        <el-form-item label="评论通知">
          <el-switch v-model="settings.commentNotify" />
        </el-form-item>
        <el-form-item label="邀请通知">
          <el-switch v-model="settings.inviteNotify" />
        </el-form-item>
        <el-form-item label="系统通知">
          <el-switch v-model="settings.systemNotify" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header><span>存储配额</span></template>
      <el-form label-width="140px">
        <el-form-item label="单用户配额(GB)">
          <el-input-number v-model="settings.userQuota" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="单家庭配额(GB)">
          <el-input-number v-model="settings.familyQuota" :min="5" :max="500" />
        </el-form-item>
        <el-form-item label="单张照片上限(MB)">
          <el-input-number v-model="settings.maxPhotoSize" :min="1" :max="50" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header><span>审核设置</span></template>
      <el-form label-width="140px">
        <el-form-item label="自动审核">
          <el-switch v-model="settings.autoReview" />
          <span style="margin-left: 12px; font-size: 12px; color: #999">开启后新照片自动通过审核</span>
        </el-form-item>
        <el-form-item label="敏感词过滤">
          <el-switch v-model="settings.sensitiveFilter" />
        </el-form-item>
      </el-form>
    </el-card>

    <div style="margin-top: 20px; text-align: right">
      <el-button type="primary" :loading="saving" @click="saveSettings">保存配置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/utils/request'

const saving = ref(false)

const settings = reactive({
  newPhotoNotify: true,
  commentNotify: true,
  inviteNotify: true,
  systemNotify: true,
  userQuota: 5,
  familyQuota: 50,
  maxPhotoSize: 10,
  autoReview: false,
  sensitiveFilter: true
})

onMounted(async () => {
  try {
    const result = await http.get<any, any>('/admin/settings')
    if (result) {
      Object.assign(settings, result)
    }
  } catch (e) {
    // 使用默认值
  }
})

async function saveSettings() {
  saving.value = true
  try {
    await http.put('/admin/settings', settings)
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.settings-page {
  max-width: 800px;
}
</style>
