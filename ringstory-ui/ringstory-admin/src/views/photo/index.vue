<template>
  <div class="photo-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>照片管理</span>
          <div class="header-actions">
            <el-select v-model="filterStatus" placeholder="审核状态" style="width: 120px" @change="fetchPhotos">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="photos" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="缩略图" width="100">
          <template #default="{ row }">
            <el-image :src="row.thumbUrl" style="width: 60px; height: 60px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传者" width="120" />
        <el-table-column prop="familyName" label="所属家庭" width="150" />
        <el-table-column prop="shootTime" label="拍摄时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="previewPhoto(row)">预览</el-button>
            <el-button v-if="row.status === 'PENDING'" size="small" type="success" @click="approvePhoto(row)">通过</el-button>
            <el-button v-if="row.status !== 'REJECTED'" size="small" type="danger" @click="rejectPhoto(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="20"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchPhotos"
        />
      </div>
    </el-card>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="照片预览" width="600px">
      <el-image v-if="previewUrl" :src="previewUrl" style="width: 100%" fit="contain" />
      <div v-if="previewData" style="margin-top: 16px">
        <p>上传者: {{ previewData.uploaderName }}</p>
        <p>拍摄时间: {{ previewData.shootTime }}</p>
        <p>家庭: {{ previewData.familyName }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/request'

const photos = ref<any[]>([])
const loading = ref(false)
const filterStatus = ref('')
const page = ref(1)
const total = ref(0)
const previewVisible = ref(false)
const previewUrl = ref('')
const previewData = ref<any>(null)

async function fetchPhotos() {
  loading.value = true
  try {
    const result = await http.get<any, any>('/admin/photos', {
      params: { page: page.value, size: 20, status: filterStatus.value }
    })
    photos.value = result?.records || result || []
    total.value = result?.total || 0
  } catch (e) {
    console.error('加载照片失败', e)
  } finally {
    loading.value = false
  }
}

function previewPhoto(row: any) {
  previewUrl.value = row.url || row.thumbUrl
  previewData.value = row
  previewVisible.value = true
}

async function approvePhoto(row: any) {
  try {
    await http.put(`/admin/photos/${row.id}/approve`)
    row.status = 'APPROVED'
    ElMessage.success('已通过')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function rejectPhoto(row: any) {
  await ElMessageBox.confirm('确定拒绝该照片？', '提示')
  try {
    await http.put(`/admin/photos/${row.id}/reject`)
    row.status = 'REJECTED'
    ElMessage.success('已拒绝')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function statusTagType(status: string): string {
  const map: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

function statusText(status: string): string {
  const map: Record<string, string> = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
  return map[status] || status
}

onMounted(() => fetchPhotos())
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
