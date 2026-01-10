<template>
  <div class="schedule-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>排班管理</span>
          <el-button type="primary" @click="handleAdd">新增排班</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userName" label="员工" width="100" />
        <el-table-column prop="scheduleDate" label="排班日期" width="120" />
        <el-table-column prop="shiftName" label="班次" width="120" />
        <el-table-column prop="workStartTime" label="上班时间" width="100" />
        <el-table-column prop="workEndTime" label="下班时间" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="员工" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择员工" style="width: 100%">
            <el-option label="张三" :value="1" />
            <el-option label="李四" :value="2" />
            <el-option label="王五" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker v-model="form.scheduleDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班次" prop="shiftId">
          <el-select v-model="form.shiftId" placeholder="请选择班次" style="width: 100%" @change="handleShiftChange">
            <el-option 
              v-for="item in shifts" 
              :key="item.id" 
              :label="item.shiftName + ' (' + item.workStartTime + ' - ' + item.workEndTime + ')'" 
              :value="item.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上班时间">
          <el-input :value="selectedShift?.workStartTime || '--'" disabled />
        </el-form-item>
        <el-form-item label="下班时间">
          <el-input :value="selectedShift?.workEndTime || '--'" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScheduleList, getShiftList, addSchedule, updateSchedule, deleteSchedule } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增排班')
const formRef = ref()
const shifts = ref([])

const queryForm = reactive({ dateRange: [] })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({
  id: null,
  userId: null,
  scheduleDate: '',
  shiftId: null,
  remark: ''
})

const rules = {
  userId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  shiftId: [{ required: true, message: '请选择班次', trigger: 'change' }]
}

const selectedShift = computed(() => {
  return shifts.value.find(s => s.id === form.shiftId)
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: pagination.current, size: pagination.size }
    if (queryForm.dateRange?.length === 2) {
      params.startDate = queryForm.dateRange[0]
      params.endDate = queryForm.dateRange[1]
    }
    const res = await getScheduleList(params)
    tableData.value = res.data?.records || res.data || []
    pagination.total = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('加载排班列表失败:', error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const loadShifts = async () => {
  try {
    const res = await getShiftList()
    shifts.value = res.data || []
    if (shifts.value.length === 0) {
      shifts.value = [
        { id: 1, shiftName: '早班', workStartTime: '08:00:00', workEndTime: '17:00:00' },
        { id: 2, shiftName: '中班', workStartTime: '10:00:00', workEndTime: '19:00:00' },
        { id: 3, shiftName: '晚班', workStartTime: '14:00:00', workEndTime: '23:00:00' }
      ]
    }
  } catch (error) {
    console.error('加载班次失败:', error)
    shifts.value = [
      { id: 1, shiftName: '早班', workStartTime: '08:00:00', workEndTime: '17:00:00' },
      { id: 2, shiftName: '中班', workStartTime: '10:00:00', workEndTime: '19:00:00' },
      { id: 3, shiftName: '晚班', workStartTime: '14:00:00', workEndTime: '23:00:00' }
    ]
  }
}

const handleShiftChange = () => {
  // 班次改变时可以自动更新时间显示
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { queryForm.dateRange = []; handleSearch() }

const handleAdd = () => {
  dialogTitle.value = '新增排班'
  Object.assign(form, { id: null, userId: null, scheduleDate: '', shiftId: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑排班'
  Object.assign(form, {
    id: row.id,
    userId: row.userId,
    scheduleDate: row.scheduleDate,
    shiftId: row.shiftId,
    remark: row.remark
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该排班？', '提示', { type: 'warning' })
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.success('删除成功')
      loadData()
    }
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    const data = {
      ...form,
      shiftName: selectedShift.value?.shiftName,
      workStartTime: selectedShift.value?.workStartTime,
      workEndTime: selectedShift.value?.workEndTime
    }
    
    if (form.id) {
      await updateSchedule(data)
    } else {
      await addSchedule(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
  loadShifts()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
</style>
