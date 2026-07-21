<template>
  <div class="user-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-input v-model="searchText" placeholder="搜索用户" prefix-icon="Search" style="width: 240px" clearable @clear="fetchUsers" @keyup.enter="fetchUsers" />
        </div>
      </template>

      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickName" label="昵称" width="150" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="familyName" label="所属家庭" width="150" />
        <el-table-column prop="photoCount" label="上传照片" width="100" />
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ row.status === 'ACTIVE' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="viewUser(row)">详情</el-button>
            <el-button size="small" :type="row.status === 'ACTIVE' ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="20"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchUsers"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/request'

const users = ref<any[]>([])
const loading = ref(false)
const searchText = ref('')
const page = ref(1)
const total = ref(0)

async function fetchUsers() {
  loading.value = true
  try {
    const result = await http.get<any, any>('/admin/users', {
      params: { page: page.value, size: 20, keyword: searchText.value }
    })
    users.value = result?.records || result || []
    total.value = result?.total || 0
  } catch (e) {
    console.error('加载用户失败', e)
  } finally {
    loading.value = false
  }
}

function viewUser(row: any) {
  ElMessageBox.alert(`用户: ${row.nickName}\n手机: ${row.phone}\n家庭: ${row.familyName || '无'}\n上传: ${row.photoCount}张`, '用户详情')
}

async function toggleStatus(row: any) {
  const action = row.status === 'ACTIVE' ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定${action}该用户？`, '提示')
  try {
    await http.put(`/admin/users/${row.id}/status`, { status: row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE' })
    row.status = row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
    ElMessage.success(`${action}成功`)
  } catch (e) {
    ElMessage.error(`${action}失败`)
  }
}

onMounted(() => fetchUsers())
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
