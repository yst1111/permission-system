<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="24">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ card.number }}</div>
              <div class="stat-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近活动 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <div class="activity-list">
            <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <el-icon :color="activity.color">
                  <component :is="activity.icon" />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统信息 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">系统版本：</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">Node版本：</span>
              <span class="info-value">v18.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">Vue版本：</span>
              <span class="info-value">v3.3.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">Element Plus：</span>
              <span class="info-value">v2.3.0</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const statCards = ref([
  {
    title: '用户总数',
    number: 156,
    icon: 'User',
    color: '#409EFF'
  },
  {
    title: '角色总数',
    number: 8,
    icon: 'UserFilled',
    color: '#67C23A'
  },
  {
    title: '菜单总数',
    number: 24,
    icon: 'Menu',
    color: '#E6A23C'
  },
  {
    title: '在线用户',
    number: 12,
    icon: 'Connection',
    color: '#F56C6C'
  }
])

const recentActivities = ref([
  {
    id: 1,
    title: '新增用户：张三',
    time: '2分钟前',
    icon: 'Plus',
    color: '#67C23A'
  },
  {
    id: 2,
    title: '修改角色权限',
    time: '5分钟前',
    icon: 'Edit',
    color: '#E6A23C'
  },
  {
    id: 3,
    title: '系统备份完成',
    time: '10分钟前',
    icon: 'Check',
    color: '#409EFF'
  },
  {
    id: 4,
    title: '用户登录：admin',
    time: '15分钟前',
    icon: 'User',
    color: '#909399'
  }
])
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.card-header {
  font-weight: bold;
  color: #303133;
}

.activity-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #F5F7FA;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.system-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #EBEEF5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #606266;
  font-size: 14px;
}

.info-value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}
</style> 