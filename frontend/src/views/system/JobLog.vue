<template>
  <div class="job-log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>定时任务执行记录</span>
        </div>
      </template>

      <!-- 筛选表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.jobName" placeholder="请输入任务名称" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 400px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="logList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="jobName" label="任务名称" width="150" />
        <el-table-column prop="jobGroup" label="任务组" width="120" />
        <el-table-column prop="targetMethod" label="目标方法" />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时(秒)" width="100">
          <template #default="scope">
            <el-tag>{{ scope.row.duration || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : scope.row.status === 'FAIL' ? 'danger' : 'warning'">
              {{ scope.row.status === 'SUCCESS' ? '成功' : scope.row.status === 'FAIL' ? '失败' : '运行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="错误信息" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const logList = ref([])

const searchForm = reactive({
  jobName: '',
  timeRange: []
})

// 获取执行记录列表
const getLogList = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.jobName) {
      params.jobName = searchForm.jobName
    }
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      params.startTimeBegin = searchForm.timeRange[0]
      params.startTimeEnd = searchForm.timeRange[1]
    }
    
    const response = await request.get('/api/job-log/list', { params })
    logList.value = response.data.data || []
  } catch (error) {
    ElMessage.error('获取执行记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  getLogList()
}

// 重置
const handleReset = () => {
  searchForm.jobName = ''
  searchForm.timeRange = []
  getLogList()
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  getLogList()
})
</script>

<style scoped>
.job-log-container {
  padding: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}
</style>
