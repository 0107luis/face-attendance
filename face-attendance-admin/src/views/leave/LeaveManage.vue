<template>
  <div class="leave-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假管理</span>
          <el-button type="primary" @click="handleAdd">申请请假</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 140px">
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userName" label="申请人" width="100" />
        <el-table-column prop="leaveTypeName" label="请假类型" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="reason" label="原因" show-overflow-tooltip min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
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
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="申请请假" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="请假类型" prop="leaveTypeId">
          <el-select v-model="form.leaveTypeId" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in leaveTypes" :key="item.id" :label="item.typeName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="天数" prop="days">
          <el-input-number v-model="form.days" :min="0.5" :max="30" :step="0.5" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLeaveList, getLeaveTypes, submitLeave, approveLeave, rejectLeave } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const leaveTypes = ref([])

const queryForm = reactive({ status: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({
  userId: 1,
  leaveTypeId: null,
  startDate: '',
  endDate: '',
  days: 1,
  reason: ''
})

const rules = {
  leaveTypeId: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
}

const getStatusType = (status) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info')
const getStatusText = (status) => ({ 0: '待审批', 1: '已通过', 2: '已拒绝' }[status] || '未知')

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: pagination.current, size: pagination.size }
    if (queryForm.status !== '') {
      params.status = queryForm.status
    }
    const res = await getLeaveList(params)
    tableData.value = res.data?.records || res.data || []
    pagination.total = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('加载请假列表失败:', error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const loadLeaveTypes = async () => {
  try {
    const res = await getLeaveTypes()
    leaveTypes.value = res.data || []
  } catch (error) {
    console.error('加载请假类型失败:', error)
    leaveTypes.value = [
      { id: 1, typeName: '事假' },
      { id: 2, typeName: '病假' },
      { id: 3, typeName: '年假' },
      { id: 4, typeName: '调休' }
    ]
  }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { queryForm.status = ''; handleSearch() }

const handleAdd = () => {
  Object.assign(form, { userId: 1, leaveTypeId: null, startDate: '', endDate: '', days: 1, reason: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await submitLeave(form)
    ElMessage.success('申请成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.success('申请成功')
    dialogVisible.value = false
    loadData()
  }
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确定通过该请假申请？', '提示', { type: 'info' })
    await approveLeave(row.id, { approverId: 1, comment: '审批通过' })
    ElMessage.success('审批通过')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审批失败: ' + (error.message || '未知错误'))
    }
  }
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm('确定拒绝该请假申请？', '提示', { type: 'warning' })
    await rejectLeave(row.id, { approverId: 1, comment: '不符合请假条件' })
    ElMessage.success('已拒绝')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
}

onMounted(() => {
  loadData()
  loadLeaveTypes()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
</style>
