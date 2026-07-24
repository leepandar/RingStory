<template>
  <div class="notification-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
          <div class="header-actions">
            <el-select v-model="filterType" placeholder="通知类型" style="width: 140px" @change="fetchNotifications">
              <el-option label="全部" value="" />
              <el-option label="新照片" value="NEW_PHOTO" />
              <el-option label="评论" value="COMMENT" />
              <el-option label="点赞" value="LIKE" />
              <el-option label="邀请" value="INVITATION" />
              <el-option label="系统" value="SYSTEM" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="notifications" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="read" label="已读" width="80">
          <template #default="{ row }">
            <el-tag :type="row.read ? 'success' : 'warning'" size="small">
              {{ row.read ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="deleteNotification(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="20"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchNotifications"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/request'

const notifications = ref<any[]>([])
const loading = ref(false)
const filterType = ref('')
const page = ref(1)
const total = ref(0)

async function fetchNotifications() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: 20 }
    if (filterType.value) params.type = filterType.value
    const result = await http.get<any, any>('/admin/notifications', { params })
    notifications.value = result?.records || result || []
    total.value = result?.total || 0
  } catch (e) {
    console.error('加载通知失败', e)
  } finally {
    loading.value = false
  }
}

async function deleteNotification(row: any) {
  await ElMessageBox.confirm('确定删除该通知？', '提示')
  try {
    await http.delete(`/admin/notifications/${row.id}`)
    ElMessage.success('已删除')
    fetchNotifications()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function typeTagType(type: string): string {
  const map: Record<string, string> = {
    NEW_PHOTO: 'success',
    COMMENT: 'warning',
    LIKE: 'danger',
    INVITATION: 'info',
    SYSTEM: ''
  }
  return map[type] || ''
}

function typeText(type: string): string {
  const map: Record<string, string> = {
    NEW_PHOTO: '新照片',
    COMMENT: '评论',
    LIKE: '点赞',
    INVITATION: '邀请',
    SYSTEM: '系统'
  }
  return map[type] || type
}

onMounted(() => fetchNotifications())
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
