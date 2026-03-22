<template>
  <div class="meal-card-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>饭卡管理</span>
        </div>
      </template>

      <!-- 饭卡列表 -->
      <el-table :data="mealCardList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="饭卡名称" width="150">
          <template #default="scope">
            <el-tag type="primary">饭卡{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalTimes" label="总次数" width="120" />
        <el-table-column prop="usedTimes" label="已用次数" width="120">
          <template #default="scope">
            <span style="color: #E6A23C;">{{ scope.row.usedTimes }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remainingTimes" label="剩余次数" width="120">
          <template #default="scope">
            <span style="color: #67C23A; font-weight: bold;">{{ scope.row.remainingTimes }}</span>
          </template>
        </el-table-column>
        <el-table-column label="使用进度" width="200">
          <template #default="scope">
            <el-progress 
              :percentage="Math.round((scope.row.usedTimes / scope.row.totalTimes) * 100)" 
              :color="getProgressColor(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="最后更新" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="success" @click="handleUse(scope.row)">使用+1</el-button>
            <el-button size="small" type="warning" @click="handleRestore(scope.row)" :disabled="scope.row.usedTimes === 0">撤销</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 菜品列表 -->
      <div class="meal-plan-section">
        <h3>菜品列表</h3>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="饭卡1" name="1">
            <el-table :data="mealPlanList1" border style="width: 100%">
              <el-table-column prop="dishName" label="菜品名称" />
              <el-table-column prop="weightG" label="重量(g)" width="100" />
              <el-table-column prop="calories" label="热量(kcal)" width="100">
                <template #default="scope">
                  <el-tag type="danger">{{ scope.row.calories }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="unitPrice" label="单价(元)" width="100" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="饭卡2" name="2">
            <el-table :data="mealPlanList2" border style="width: 100%">
              <el-table-column prop="dishName" label="菜品名称" />
              <el-table-column prop="weightG" label="重量(g)" width="100" />
              <el-table-column prop="calories" label="热量(kcal)" width="100">
                <template #default="scope">
                  <el-tag type="danger">{{ scope.row.calories }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="unitPrice" label="单价(元)" width="100" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑饭卡" width="400px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="总次数">
          <el-input-number v-model="editForm.totalTimes" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="已用次数">
          <el-input-number v-model="editForm.usedTimes" :min="0" :max="editForm.totalTimes" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const mealCardList = ref([])
const mealPlanList1 = ref([])
const mealPlanList2 = ref([])
const activeTab = ref('1')
const dialogVisible = ref(false)
const editForm = reactive({
  id: '',
  totalTimes: 20,
  usedTimes: 0
})

// 获取饭卡列表
const getMealCardList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/meal-card/list')
    mealCardList.value = response.data || response
  } catch (error) {
    ElMessage.error('获取饭卡列表失败')
  } finally {
    loading.value = false
  }
}

// 获取菜品列表
const getMealPlanList = async () => {
  try {
    const response = await request.get('/api/meal-plan/list')
    const list = response.data || response
    mealPlanList1.value = list.filter(item => item.cardId === 1)
    mealPlanList2.value = list.filter(item => item.cardId === 2)
  } catch (error) {
    // 静默处理
  }
}

// 使用+1
const handleUse = async (row) => {
  try {
    await request.put(`/api/meal-card/use/${row.id}`)
    ElMessage.success('使用成功')
    getMealCardList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 撤销
const handleRestore = async (row) => {
  try {
    await request.put(`/api/meal-card/restore/${row.id}`)
    ElMessage.success('撤销成功')
    getMealCardList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 编辑
const handleEdit = (row) => {
  editForm.id = row.id
  editForm.totalTimes = row.totalTimes
  editForm.usedTimes = row.usedTimes
  dialogVisible.value = true
}

// 提交编辑
const handleSubmit = async () => {
  try {
    const remainingTimes = editForm.totalTimes - editForm.usedTimes
    await request.put('/api/meal-card/update', {
      id: editForm.id,
      totalTimes: editForm.totalTimes,
      usedTimes: editForm.usedTimes,
      remainingTimes: remainingTimes
    })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    getMealCardList()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 获取进度条颜色
const getProgressColor = (row) => {
  const percentage = (row.usedTimes / row.totalTimes) * 100
  if (percentage >= 80) return '#F56C6C'
  if (percentage >= 50) return '#E6A23C'
  return '#67C23A'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  getMealCardList()
  getMealPlanList()
})
</script>

<style scoped>
.meal-card-container {
  padding: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.meal-plan-section {
  margin-top: 30px;
}

.meal-plan-section h3 {
  margin-bottom: 15px;
  color: #303133;
}
</style>
