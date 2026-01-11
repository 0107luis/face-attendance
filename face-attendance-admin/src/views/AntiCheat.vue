<template>
  <div class="anticheat">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>防作弊记录</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="风险等级">
          <el-select v-model="queryForm.riskLevel" placeholder="请选择" clearable>
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
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
        <el-table-column prop="deviceId" label="设备ID" />
        <el-table-column prop="ipAddress" label="IP地址" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="checkType" label="考勤类型">
          <template #default="{ row }">
            <el-tag>{{ row.checkType === 'checkIn' ? '签到' : '签退' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkTime" label="考勤时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
              {{ row.status === 'normal' ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级">
          <template #default="{ row }">
            <el-tag :type="getRiskType(row.riskLevel)">
              {{ getRiskText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskReason" label="风险原因" show-overflow-tooltip />
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

const tableData = ref([])
const queryForm = reactive({
  riskLevel: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getRiskType = (level) => {
  const map = { low: 'success', medium: 'warning', high: 'danger' }
  return map[level] || 'info'
}

const getRiskText = (level) => {
  const map = { low: '低风险', medium: '中风险', high: '高风险' }
  return map[level] || level
}

const loadData = async () => {
  tableData.value = [
    { id: 1, userId: 2, deviceId: 'device001', ipAddress: '192.168.1.100', location: '公司', checkType: 'checkIn', checkTime: '2026-03-02 09:00:00', status: 'normal', riskLevel: 'low', riskReason: '' },
    { id: 2, userId: 3, deviceId: 'device002', ipAddress: '192.168.1.101', location: '公司', checkType: 'checkIn', checkTime: '2026-03-02 09:05:00', status: 'normal', riskLevel: 'low', riskReason: '' },
    { id: 3, userId: 4, deviceId: 'device003', ipAddress: '10.0.0.1', location: '异地', checkType: 'checkIn', checkTime: '2026-03-02 09:10:00', status: 'abnormal', riskLevel: 'high', riskReason: 'GPS位置异常' }
  ]
  pagination.total = 3
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  queryForm.riskLevel = ''
  handleSearch()
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
