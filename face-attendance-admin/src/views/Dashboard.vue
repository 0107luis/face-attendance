<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409EFF">
            <el-icon size="30"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">今日签到</div>
            <div class="stat-value">{{ stats.todayCheckIn }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67C23A">
            <el-icon size="30"><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">正常考勤</div>
            <div class="stat-value">{{ stats.normal }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #E6A23C">
            <el-icon size="30"><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">迟到</div>
            <div class="stat-value">{{ stats.late }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #F56C6C">
            <el-icon size="30"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">待审批</div>
            <div class="stat-value">{{ stats.pendingAppeal }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>本周考勤趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考勤状态分布</span>
            </div>
          </template>
          <div ref="pieChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'

const trendChartRef = ref()
const pieChartRef = ref()

const stats = reactive({
  todayCheckIn: 0,
  normal: 0,
  late: 0,
  pendingAppeal: 0
})

onMounted(() => {
  stats.todayCheckIn = 45
  stats.normal = 38
  stats.late = 5
  stats.pendingAppeal = 2

  initTrendChart()
  initPieChart()
})

const initTrendChart = () => {
  const chart = echarts.init(trendChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '签到人数',
        type: 'line',
        data: [42, 45, 43, 48, 46, 20, 15],
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      }
    ]
  })
}

const initPieChart = () => {
  const chart = echarts.init(pieChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [
      {
        name: '考勤状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' }
        },
        data: [
          { value: 38, name: '正常', itemStyle: { color: '#67C23A' } },
          { value: 5, name: '迟到', itemStyle: { color: '#E6A23C' } },
          { value: 2, name: '早退', itemStyle: { color: '#909399' } },
          { value: 3, name: '缺勤', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  })
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.card-header {
  font-weight: bold;
}
</style>
