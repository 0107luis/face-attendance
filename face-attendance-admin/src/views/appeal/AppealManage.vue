<template>
  <div class="appeal-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>申诉管理</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="用户ID" />
        <el-table-column prop="recordId" label="考勤记录ID" />
        <el-table-column prop="appealType" label="申诉类型">
          <template #default="{ row }">
            <el-tag>{{ getAppealType(row.appealType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申诉理由" show-overflow-tooltip />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewer" label="审核人" />
        <el-table-column prop="reviewComment" label="审核意见" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" link @click="handleApprove(row)">通过</el-button>
              <el-button type="danger" link @click="handleReject(row)">拒绝</el-button>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppealList, approveAppeal, rejectAppeal } from '@/api'

const tableData = ref([])
const queryForm = reactive({
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getAppealType = (type) => {
  const map = { late: '迟到', absent: '缺勤', early: '早退', other: '其他' }
  return map[type] || type
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

const loadData = async () => {
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      ...(queryForm.status !== '' && { status: queryForm.status })
    }
    const res = await getAppealList(params)
    tableData.value = res.data.records || res.data || []
    pagination.total = res.data.total || tableData.value.length
  } catch (error) {
    console.error('加载申诉列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  queryForm.status = ''
  handleSearch()
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要通过该申诉吗？', '提示', { type: 'info' })
    await approveAppeal(row.id, { reviewer: 'admin', comment: '已审核通过' })
    ElMessage.success('审核通过')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败')
    }
  }
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该申诉吗？', '提示', { type: 'warning' })
    await rejectAppeal(row.id, { reviewer: 'admin', comment: '不符合考勤规定' })
    ElMessage.success('已拒绝')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}
</style>
