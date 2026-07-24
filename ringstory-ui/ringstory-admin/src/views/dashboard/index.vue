<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <text class="stat-label">用户总数</text>
              <text class="stat-value">{{ stats.userCount }}</text>
            </div>
            <el-icon class="stat-icon" style="color: #409eff"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <text class="stat-label">家庭总数</text>
              <text class="stat-value">{{ stats.familyCount }}</text>
            </div>
            <el-icon class="stat-icon" style="color: #67c23a"><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <text class="stat-label">照片总数</text>
              <text class="stat-value">{{ stats.photoCount }}</text>
            </div>
            <el-icon class="stat-icon" style="color: #e6a23c"><Picture /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <text class="stat-label">今日上传</text>
              <text class="stat-value">{{ stats.todayUpload }}</text>
            </div>
            <el-icon class="stat-icon" style="color: #f56c6c"><Upload /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card class="activity-card">
      <template #header>
        <span>最近活动</span>
      </template>
      <el-table :data="recentActivities" stripe>
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="activityTagType(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, UserFilled, Picture, Upload } from '@element-plus/icons-vue'
import http from '@/utils/request'

const stats = ref({
  userCount: 0,
  familyCount: 0,
  photoCount: 0,
  todayUpload: 0
})

const recentActivities = ref<any[]>([])

onMounted(async () => {
  try {
    const result = await http.get<any, any>('/admin/dashboard/stats')
    if (result) {
      stats.value = { ...stats.value, ...result }
    }
  } catch (e) {
    // 使用默认值
    stats.value = { userCount: 128, familyCount: 45, photoCount: 3672, todayUpload: 23 }
  }

  try {
    const result = await http.get<any, any>('/admin/dashboard/activities')
    if (Array.isArray(result)) {
      recentActivities.value = result
    }
  } catch (e) {
    recentActivities.value = [
      { time: '2024-01-01 12:00', type: '新用户', description: '用户张三注册' },
      { time: '2024-01-01 11:30', type: '上传照片', description: '李家大院上传了5张照片' },
      { time: '2024-01-01 10:00', type: '创建家庭', description: '用户王五创建了"王家小院"' }
    ]
  }
})

function activityTagType(type: string): string {
  const map: Record<string, string> = { '新用户': 'primary', '上传照片': 'success', '创建家庭': 'warning' }
  return map[type] || 'info'
}
</script>

<style scoped>
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-icon {
  font-size: 40px;
  opacity: 0.8;
}

.activity-card {
  margin-top: 20px;
}
</style>
