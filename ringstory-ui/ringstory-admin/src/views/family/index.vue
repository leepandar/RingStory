<template>
  <div class="family-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>家庭管理</span>
          <el-input v-model="searchText" placeholder="搜索家庭" prefix-icon="Search" style="width: 240px" clearable @clear="fetchFamilies" @keyup.enter="fetchFamilies" />
        </div>
      </template>

      <el-table :data="families" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="家庭名称" width="180" />
        <el-table-column prop="ownerName" label="户主" width="120" />
        <el-table-column prop="memberCount" label="成员数" width="100" />
        <el-table-column prop="photoCount" label="照片数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewMembers(row)">成员</el-button>
            <el-button size="small" type="danger" @click="deleteFamily(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="20"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchFamilies"
        />
      </div>
    </el-card>

    <!-- 成员弹窗 -->
    <el-dialog v-model="memberDialogVisible" title="家庭成员" width="500px">
      <el-table :data="currentMembers" stripe>
        <el-table-column prop="nickName" label="昵称" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'OWNER' ? 'warning' : 'info'">
              {{ row.role === 'OWNER' ? '户主' : '成员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/request'

const families = ref<any[]>([])
const loading = ref(false)
const searchText = ref('')
const page = ref(1)
const total = ref(0)
const memberDialogVisible = ref(false)
const currentMembers = ref<any[]>([])

async function fetchFamilies() {
  loading.value = true
  try {
    const result = await http.get<any, any>('/admin/families', {
      params: { page: page.value, size: 20, keyword: searchText.value }
    })
    families.value = result?.records || result || []
    total.value = result?.total || 0
  } catch (e) {
    console.error('加载家庭失败', e)
  } finally {
    loading.value = false
  }
}

async function viewMembers(row: any) {
  try {
    const result = await http.get<any, any>(`/family/${row.id}/members`)
    currentMembers.value = Array.isArray(result) ? result : []
    memberDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载成员失败')
  }
}

async function deleteFamily(row: any) {
  await ElMessageBox.confirm(`确定删除家庭"${row.name}"？此操作不可恢复！`, '警告', { type: 'warning' })
  try {
    await http.delete(`/admin/families/${row.id}`)
    ElMessage.success('删除成功')
    fetchFamilies()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => fetchFamilies())
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
