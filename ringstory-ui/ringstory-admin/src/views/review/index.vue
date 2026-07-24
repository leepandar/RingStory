<template>
  <div class="review-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>回顾管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="triggerGenerate">手动生成回顾</el-button>
          </div>
        </div>
      </template>

      <el-table :data="reviews" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="familyId" label="家庭ID" width="100" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="period" label="周期" width="160" />
        <el-table-column prop="photoCount" label="照片数" width="100" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverUrl" :src="row.coverUrl" style="width: 60px; height: 60px" fit="cover" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'">
              {{ row.status === 'COMPLETED' ? '已完成' : '生成中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="previewReview(row)">预览</el-button>
            <el-button size="small" type="danger" @click="deleteReview(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="20"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchReviews"
        />
      </div>
    </el-card>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="回顾预览" width="600px">
      <div v-if="previewData">
        <h3>{{ previewData.title }}</h3>
        <p>类型: {{ typeText(previewData.type) }} | 周期: {{ previewData.period }}</p>
        <p>照片数: {{ previewData.photoCount }}</p>
        <el-image v-if="previewData.posterUrl" :src="previewData.posterUrl" style="width: 100%; margin-top: 16px" fit="contain" />
        <div v-if="previewData.videoUrl" style="margin-top: 16px">
          <video :src="previewData.videoUrl" controls style="width: 100%" />
        </div>
      </div>
    </el-dialog>

    <!-- 手动生成弹窗 -->
    <el-dialog v-model="generateVisible" title="手动生成回顾" width="400px">
      <el-form :model="generateForm" label-width="80px">
        <el-form-item label="家庭ID">
          <el-input v-model="generateForm.familyId" placeholder="输入家庭ID" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="generateForm.type" placeholder="选择类型">
            <el-option label="月度回顾" value="MONTHLY" />
            <el-option label="季度回顾" value="SEASONAL" />
            <el-option label="年度回顾" value="YEARLY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/request'

const reviews = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const previewVisible = ref(false)
const previewData = ref<any>(null)
const generateVisible = ref(false)
const generateForm = reactive({ familyId: '', type: 'MONTHLY' })

async function fetchReviews() {
  loading.value = true
  try {
    const result = await http.get<any, any>('/admin/reviews', {
      params: { page: page.value, size: 20 }
    })
    reviews.value = result?.records || result || []
    total.value = result?.total || 0
  } catch (e) {
    console.error('加载回顾失败', e)
  } finally {
    loading.value = false
  }
}

function previewReview(row: any) {
  previewData.value = row
  previewVisible.value = true
}

async function deleteReview(row: any) {
  await ElMessageBox.confirm('确定删除该回顾？', '提示')
  try {
    await http.delete(`/admin/reviews/${row.id}`)
    ElMessage.success('已删除')
    fetchReviews()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function triggerGenerate() {
  generateVisible.value = true
}

async function confirmGenerate() {
  if (!generateForm.familyId) {
    ElMessage.warning('请输入家庭ID')
    return
  }
  try {
    await http.post('/admin/reviews/generate', generateForm)
    ElMessage.success('回顾生成任务已提交')
    generateVisible.value = false
    fetchReviews()
  } catch (e) {
    ElMessage.error('生成失败')
  }
}

function typeTagType(type: string): string {
  const map: Record<string, string> = { MONTHLY: '', SEASONAL: 'warning', YEARLY: 'danger' }
  return map[type] || ''
}

function typeText(type: string): string {
  const map: Record<string, string> = { MONTHLY: '月度', SEASONAL: '季度', YEARLY: '年度' }
  return map[type] || type
}

onMounted(() => fetchReviews())
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
