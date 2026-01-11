<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考勤统计分析</span>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                @change="handleDateChange"
              />
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">应考勤人数</div>
                <div class="stat-value">50</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">实际签到人数</div>
                <div class="stat-value">45</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">出勤率</div>
                <div class="stat-value">90%</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-label">平均工作时长</div>
                <div class="stat-value">520分钟</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>部门考勤统计</span>
            </div>
          </template>
          <div ref="deptChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考勤类型分布</span>
            </div>
          </template>
          <div ref="typeChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考勤明细</span>
            </div>
          </template>
          <el-table :data="tableData" border stripe>
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="total" label="应考勤人数" />
            <el-table-column prop="actual" label="实际签到" />
            <el-table-column prop="late" label="迟到" />
            <el-table-column prop="early" label="早退" />
            <el-table-column prop="absent" label="缺勤" />
            <el-table-column prop="rate" label="出勤率">
              <template #default="{ row }">
                <el-progress :percentage="row.rate" :color="getProgressColor(row.rate)" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const dateRange = ref([])
const deptChartRef = ref()
const typeChartRef = ref()

const tableData = ref([
  { department: '技术部', total: 20, actual: 19, late: 1, early: 0, absent: 0, rate: 95 },
  { department: '研发部', total: 15, actual: 14, late: 1, early: 0, absent: 0, rate: 93 },
  { department: '销售部', total: 10, actual: 8, late: 1, early: 1, absent: 0, rate: 80 },
  { department: '运营部', total: 5, actual: 4, late: 0, early: 0, absent: 1, rate: 80 }
])

const getProgressColor = (rate) => {
  if (rate >= 90) return '#67C23A'
  if (rate >= 70) return '#E6A23C'
  return '#F56C6C'
}

const handleDateChange = (val) => {
  console.log('日期范围:', val)
}

const initDeptChart = () => {
  const chart = echarts.init(deptChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { bottom: '0%' },
    xAxis: { type: 'category', data: ['技术部', '研发部', '销售部', '运营部'] },
    yAxis: { type: 'value' },
    series: [
      { name: '实际签到', type: 'bar', data: [19, 14, 8, 4], itemStyle: { color: '#67C23A' } },
      { name: '迟到', type: 'bar', data: [1, 1, 1, 0], itemStyle: { color: '#E6A23C' } },
      { name: '早退', type: 'bar', data: [0, 0, 1, 0], itemStyle: { color: '#909399' } },
      { name: '缺勤', type: 'bar', data: [0, 0, 0, 1], itemStyle: { color: '#F56C6C' } }
    ]
  })
}

const initTypeChart = () => {
  const chart = echarts.init(typeChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [
      {
        name: '考勤类型',
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 40, name: '正常', itemStyle: { color: '#67C23A' } },
          { value: 3, name: '迟到', itemStyle: { color: '#E6A23C' } },
          { value: 1, name: '早退', itemStyle: { color: '#909399' } },
          { value: 1, name: '缺勤', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  })
}

onMounted(() => {
  initDeptChart()
  initTypeChart()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
