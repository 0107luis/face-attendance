<template>
  <div class="rule-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤规则</span>
          <el-button type="primary" @click="handleAdd">新增规则</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="ruleName" label="规则名称" />
        <el-table-column prop="workStartTime" label="上班时间" />
        <el-table-column prop="workEndTime" label="下班时间" />
        <el-table-column prop="lateThreshold" label="迟到阈值(分钟)" />
        <el-table-column prop="earlyThreshold" label="早退阈值(分钟)" />
        <el-table-column prop="flexibleMinutes" label="弹性时间(分钟)" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="上班时间" prop="workStartTime">
              <el-time-picker v-model="form.workStartTime" format="HH:mm" value-format="HH:mm:ss" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下班时间" prop="workEndTime">
              <el-time-picker v-model="form.workEndTime" format="HH:mm" value-format="HH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="迟到阈值" prop="lateThreshold">
              <el-input-number v-model="form.lateThreshold" :min="1" :max="60" />
              <span style="margin-left: 5px">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="早退阈值" prop="earlyThreshold">
              <el-input-number v-model="form.earlyThreshold" :min="1" :max="60" />
              <span style="margin-left: 5px">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="缺勤阈值" prop="absentThreshold">
              <el-input-number v-model="form.absentThreshold" :min="1" :max="120" />
              <span style="margin-left: 5px">分钟</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="弹性打卡时间">
          <el-input-number v-model="form.flexibleMinutes" :min="0" :max="30" />
          <span style="margin-left: 5px">分钟</span>
        </el-form-item>
        <el-form-item label="标准工作时长">
          <el-input-number v-model="form.workHours" :min="1" :max="12" />
          <span style="margin-left: 5px">小时</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增规则')
const formRef = ref()

const form = reactive({
  id: null,
  ruleName: '',
  workStartTime: '09:00:00',
  workEndTime: '18:00:00',
  lateThreshold: 15,
  earlyThreshold: 15,
  absentThreshold: 60,
  flexibleMinutes: 0,
  workHours: 8,
  status: 1
})

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  workStartTime: [{ required: true, message: '请选择上班时间', trigger: 'change' }],
  workEndTime: [{ required: true, message: '请选择下班时间', trigger: 'change' }]
}

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      { id: 1, ruleName: '默认考勤规则', workStartTime: '09:00:00', workEndTime: '18:00:00', lateThreshold: 15, earlyThreshold: 15, absentThreshold: 60, flexibleMinutes: 0, workHours: 8, status: 1 },
      { id: 2, ruleName: '弹性工作制', workStartTime: '08:30:00', workEndTime: '17:30:00', lateThreshold: 30, earlyThreshold: 30, absentThreshold: 60, flexibleMinutes: 30, workHours: 8, status: 1 }
    ]
    loading.value = false
  }, 500)
}

const handleAdd = () => {
  dialogTitle.value = '新增规则'
  Object.assign(form, { id: null, ruleName: '', workStartTime: '09:00:00', workEndTime: '18:00:00', lateThreshold: 15, earlyThreshold: 15, absentThreshold: 60, flexibleMinutes: 0, workHours: 8, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑规则'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该规则？', '提示', { type: 'warning' })
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {}
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
