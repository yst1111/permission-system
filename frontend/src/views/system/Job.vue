<template>
  <div class="job-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>定时任务管理</span>
          <el-button type="primary" @click="handleAdd">新增任务</el-button>
        </div>
      </template>

      <el-table :data="jobList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="jobName" label="任务名称" width="150" />
        <el-table-column prop="jobGroup" label="任务组" width="120" />
        <el-table-column prop="cronExpression" label="Cron表达式" width="150" />
        <el-table-column prop="targetMethod" label="目标方法" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" :type="scope.row.status === 1 ? 'warning' : 'success'" @click="handleStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务组" prop="jobGroup">
          <el-input v-model="form.jobGroup" placeholder="请输入任务组" />
        </el-form-item>
        <el-form-item label="Cron表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="如: 0/5 * * * * ?" />
        </el-form-item>
        <el-form-item label="目标方法" prop="targetMethod">
          <el-input v-model="form.targetMethod" placeholder="请输入目标方法" />
        </el-form-item>
        <el-form-item label="目标参数" prop="targetParams">
          <el-input v-model="form.targetParams" placeholder="请输入目标参数(可选)" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" />
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
import request from '@/utils/request'

const loading = ref(false)
const jobList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  jobName: '',
  jobGroup: 'DEFAULT',
  cronExpression: '',
  targetMethod: '',
  targetParams: '',
  status: 1,
  description: ''
})

const rules = {
  jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  cronExpression: [{ required: true, message: '请输入Cron表达式', trigger: 'blur' }],
  targetMethod: [{ required: true, message: '请输入目标方法', trigger: 'blur' }]
}

// 获取任务列表
const getJobList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/job/list')
    jobList.value = response.data.data || []
  } catch (error) {
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.jobName = ''
  form.jobGroup = 'DEFAULT'
  form.cronExpression = ''
  form.targetMethod = ''
  form.targetParams = ''
  form.status = 1
  form.description = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await request.put('/api/job', form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/api/job', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    getJobList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', { type: 'warning' })
    await request.delete(`/api/job/${row.id}`)
    ElMessage.success('删除成功')
    getJobList()
  } catch (error) {
    // 用户取消
  }
}

// 修改状态
const handleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await request.put(`/api/job/status/${row.id}?status=${newStatus}`)
    ElMessage.success(newStatus === 1 ? '启用成功' : '禁用成功')
    getJobList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  getJobList()
})
</script>

<style scoped>
.job-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
