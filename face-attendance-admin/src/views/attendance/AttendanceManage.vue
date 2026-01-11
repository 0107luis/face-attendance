<template>
  <div class="attendance-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤记录</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="日期">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryForm.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="迟到" value="late" />
            <el-option label="早退" value="early" />
            <el-option label="缺勤" value="absent" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="employeeNo" label="工号" />
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="attendanceDate" label="考勤日期" />
        <el-table-column prop="checkInTime" label="签到时间" />
        <el-table-column prop="checkOutTime" label="签退时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workDuration" label="工作时长(分钟)" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
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
import { ElMessage } from 'element-plus'
import { getAttendanceList } from '@/api'

const tableData = ref([])
const queryForm = reactive({
  dateRange: [],
  realName: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getStatusType = (status) => {
  const map = { normal: 'success', late: 'warning', early: 'info', absent: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { normal: '正常', late: '迟到', early: '早退', absent: '缺勤' }
  return map[status] || '未知'
}

const loadData = async () => {
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      ...(queryForm.realName && { realName: queryForm.realName }),
      ...(queryForm.status && { status: queryForm.status }),
      ...(queryForm.dateRange && queryForm.dateRange.length === 2 && { 
        startDate: queryForm.dateRange[0],
        endDate: queryForm.dateRange[1]
      })
    }
    const res = await getAttendanceList(params)
    tableData.value = res.data.records || res.data || []
    pagination.total = res.data.total || tableData.value.length
  } catch (error) {
    console.error('加载考勤记录失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  queryForm.dateRange = []
  queryForm.realName = ''
  queryForm.status = ''
  handleSearch()
}

const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }
  const headers = ['ID', '姓名', '工号', '部门', '考勤日期', '签到时间', '签退时间', '状态', '工作时长(分钟)']
  const rows = tableData.value.map(item => [
    item.id, item.realName, item.employeeNo, item.department, item.attendanceDate,
    item.checkInTime || '-', item.checkOutTime || '-', getStatusText(item.status), item.workDuration || 0
  ])
  
  let csvContent = '\ufeff' + headers.join(',') + '\n'
  rows.forEach(row => {
    csvContent += row.map(cell => `"${cell}"`).join(',') + '\n'
  })
  
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `考勤记录_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
  ElMessage.success('导出成功')
}

const handleDetail = (row) => {
  console.log('查看详情:', row)
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
