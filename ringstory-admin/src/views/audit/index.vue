<template>
  <div class="audit-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审计日志</span>
          <div class="header-actions">
            <el-select v-model="filterAction" placeholder="操作类型" style="width: 140px" @change="fetchLogs">
              <el-option label="全部" value="" />
              <el-option label="删除照片" value="DELETE_PHOTO" />
              <el-option label="移除成员" value="REMOVE_MEMBER" />
              <el-option label="权限变更" value="CHANGE_ROLE" />
              <el-option label="导出照片" value="EXPORT_PHOTO" />
              <el-option label="其他" value="OTHER" />
            </el-select>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 260px"
              @change="fetchLogs"
            />
          </div>
        </div>
      </template>

      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="action" label="操作类型" width="140">
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.action)">{{ actionText(row.action) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="目标类型" width="120" />
        <el-table-column prop="targetId" label="目标ID" width="100" />
        <el-table-column prop="familyId" label="家庭ID" width="100" />
        <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="20"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import http from '@/utils/request'

const logs = ref<any[]>([])
const loading = ref(false)
const filterAction = ref('')
const dateRange = ref<[string, string] | null>(null)
const page = ref(1)
const total = ref(0)

async function fetchLogs() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: 20 }
    if (filterAction.value) params.action = filterAction.value
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const result = await http.get<any, any>('/admin/audit-logs', { params })
    logs.value = result?.records || result || []
    total.value = result?.total || 0
  } catch (e) {
    console.error('加载审计日志失败', e)
  } finally {
    loading.value = false
  }
}

function actionTagType(action: string): string {
  const map: Record<string, string> = {
    DELETE_PHOTO: 'danger',
    REMOVE_MEMBER: 'warning',
    CHANGE_ROLE: 'info',
    EXPORT_PHOTO: 'success'
  }
  return map[action] || ''
}

function actionText(action: string): string {
  const map: Record<string, string> = {
    DELETE_PHOTO: '删除照片',
    REMOVE_MEMBER: '移除成员',
    CHANGE_ROLE: '权限变更',
    EXPORT_PHOTO: '导出照片',
    OTHER: '其他'
  }
  return map[action] || action
}

onMounted(() => fetchLogs())
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
