<template>
  <div class="user-import-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户导入</span>
          <el-button type="primary" link @click="$router.go(-1)">返回</el-button>
        </div>
      </template>
      
      <!-- 导入步骤 -->
      <el-steps :active="currentStep" finish-status="success" class="import-steps">
        <el-step title="上传文件" description="选择要导入的Excel文件" />
        <el-step title="数据预览" description="预览导入的数据内容" />
        <el-step title="确认导入" description="确认并执行导入操作" />
      </el-steps>
      
      <!-- 步骤1：文件上传 -->
      <div v-if="currentStep === 0" class="step-content">
        <div class="upload-area">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            accept=".xlsx,.xls"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 xlsx/xls 文件，且不超过 10MB
              </div>
            </template>
          </el-upload>
        </div>
        
        <div class="template-download">
          <h4>导入说明：</h4>
          <p>1. 请使用标准模板格式，确保数据格式正确</p>
          <p>2. 用户名必须唯一，不能重复</p>
          <p>3. 密码为空时将使用默认密码：123456</p>
          <p>4. 状态默认为启用(1)，性别默认为未知(0)</p>
          <p>5. 必填字段：用户名、昵称</p>
          <el-button type="primary" @click="downloadTemplate">下载导入模板</el-button>
        </div>
        
        <div class="step-actions">
          <el-button type="primary" @click="nextStep" :disabled="!selectedFile">
            下一步
          </el-button>
        </div>
      </div>
      
      <!-- 步骤2：数据预览 -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="preview-header">
          <h3>数据预览 (共 {{ previewData.length }} 条)</h3>
          <el-button type="primary" @click="prevStep">上一步</el-button>
        </div>
        
        <div class="preview-table">
          <el-table :data="previewData" border style="width: 100%" max-height="400">
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="scope">
                <el-tag v-if="scope.row.gender === 1" type="primary">男</el-tag>
                <el-tag v-else-if="scope.row.gender === 2" type="danger">女</el-tag>
                <el-tag v-else type="info">未知</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
                <el-tag v-else type="danger">禁用</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column prop="error" label="错误信息" width="200">
              <template #default="scope">
                <span v-if="scope.row.error" class="error-text">{{ scope.row.error }}</span>
                <span v-else class="success-text">✓</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="validation-summary">
          <el-alert
            v-if="validationErrors.length > 0"
            :title="`发现 ${validationErrors.length} 个错误`"
            type="error"
            :closable="false"
            show-icon
          >
            <template #default>
              <ul>
                <li v-for="(error, index) in validationErrors" :key="index">
                  {{ error }}
                </li>
              </ul>
            </template>
          </el-alert>
          
          <el-alert
            v-else
            title="数据验证通过，可以继续导入"
            type="success"
            :closable="false"
            show-icon
          />
        </div>
        
        <div class="step-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button type="primary" @click="nextStep" :disabled="validationErrors.length > 0">
            下一步
          </el-button>
        </div>
      </div>
      
      <!-- 步骤3：确认导入 -->
      <div v-if="currentStep === 2" class="step-content">
        <div class="confirm-content">
          <el-result
            icon="success"
            title="数据验证完成"
            :sub-title="`共 ${previewData.length} 条数据，${validationErrors.length} 个错误`"
          >
            <template #extra>
              <div class="import-summary">
                <h4>导入摘要：</h4>
                <p>• 总数据量：{{ previewData.length }} 条</p>
                <p>• 有效数据：{{ validDataCount }} 条</p>
                <p>• 错误数据：{{ validationErrors.length }} 条</p>
                <p>• 预计耗时：{{ estimatedTime }} 秒</p>
              </div>
              
              <div class="step-actions">
                <el-button @click="prevStep">上一步</el-button>
                <el-button type="primary" @click="executeImport" :loading="importing">
                  开始导入
                </el-button>
              </div>
            </template>
          </el-result>
        </div>
      </div>
      
      <!-- 导入结果 -->
      <div v-if="importResult" class="import-result">
        <el-result
          :icon="importResult.success ? 'success' : 'error'"
          :title="importResult.success ? '导入完成' : '导入失败'"
          :sub-title="importResult.message"
        >
          <template #extra>
            <div v-if="importResult.success" class="result-details">
              <p>• 成功导入：{{ importResult.successCount }} 条</p>
              <p>• 失败数据：{{ importResult.failCount }} 条</p>
              <p>• 总耗时：{{ importResult.duration }} 秒</p>
            </div>
            
            <div class="step-actions">
              <el-button @click="$router.go(-1)">返回用户列表</el-button>
              <el-button type="primary" @click="resetImport">重新导入</el-button>
            </div>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 响应式数据
const currentStep = ref(0)
const fileList = ref([])
const selectedFile = ref(null)
const previewData = ref([])
const validationErrors = ref([])
const importing = ref(false)
const importResult = ref(null)

// 计算属性
const validDataCount = computed(() => {
  return previewData.value.filter(item => !item.error).length
})

const estimatedTime = computed(() => {
  return Math.ceil(previewData.value.length / 10) // 假设每秒处理10条数据
})

// 文件选择
const handleFileChange = (file) => {
  selectedFile.value = file
  fileList.value = [file]
}

// 下载模板
const downloadTemplate = () => {
  // 创建模板数据
  const templateData = [
    ['用户名*', '昵称*', '邮箱', '手机号', '性别(0-未知,1-男,2-女)', '状态(0-禁用,1-启用)', '备注'],
    ['testuser1', '测试用户1', 'test1@example.com', '13800138001', '1', '1', '测试用户'],
    ['testuser2', '测试用户2', 'test2@example.com', '13800138002', '2', '1', '测试用户']
  ]
  
  // 转换为CSV格式
  const csvContent = templateData.map(row => row.join(',')).join('\n')
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = '用户导入模板.csv'
  link.click()
}

// 下一步
const nextStep = async () => {
  if (currentStep.value === 0) {
    // 解析Excel文件
    await parseExcelFile()
  } else if (currentStep.value === 1) {
    // 验证数据
    validateData()
  }
  
  if (currentStep.value < 2) {
    currentStep.value++
  }
}

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 解析Excel文件
const parseExcelFile = async () => {
  try {
    // 这里应该调用后端API解析Excel文件
    // 暂时使用模拟数据
    previewData.value = [
      {
        username: 'testuser1',
        nickname: '测试用户1',
        email: 'test1@example.com',
        phone: '13800138001',
        gender: 1,
        status: 1,
        remark: '测试用户'
      },
      {
        username: 'testuser2',
        nickname: '测试用户2',
        email: 'test2@example.com',
        phone: '13800138002',
        gender: 2,
        status: 1,
        remark: '测试用户'
      }
    ]
    
    ElMessage.success('文件解析成功')
  } catch (error) {
    ElMessage.error('文件解析失败：' + error.message)
  }
}

// 验证数据
const validateData = () => {
  validationErrors.value = []
  
  previewData.value.forEach((item, index) => {
    item.error = null
    
    // 验证用户名
    if (!item.username || item.username.length < 3 || item.username.length > 20) {
      item.error = '用户名长度必须在3-20个字符之间'
      validationErrors.value.push(`第${index + 1}行：用户名长度必须在3-20个字符之间`)
    }
    
    // 验证昵称
    if (!item.nickname || item.nickname.length > 50) {
      item.error = '昵称长度不能超过50个字符'
      validationErrors.value.push(`第${index + 1}行：昵称长度不能超过50个字符`)
    }
    
    // 验证邮箱格式
    if (item.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(item.email)) {
      item.error = '邮箱格式不正确'
      validationErrors.value.push(`第${index + 1}行：邮箱格式不正确`)
    }
    
    // 验证手机号格式
    if (item.phone && !/^1[3-9]\d{9}$/.test(item.phone)) {
      item.error = '手机号格式不正确'
      validationErrors.value.push(`第${index + 1}行：手机号格式不正确`)
    }
  })
}

// 执行导入
const executeImport = async () => {
  try {
    importing.value = true
    
    // 调用后端导入API
    const validData = previewData.value.filter(item => !item.error)
    const response = await request.post('/user/batch/import', validData)
    
    const startTime = Date.now()
    
    // 模拟导入过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    const duration = Math.round((Date.now() - startTime) / 1000)
    
    importResult.value = {
      success: true,
      message: `成功导入 ${validData.length} 条用户数据`,
      successCount: validData.length,
      failCount: 0,
      duration: duration
    }
    
    ElMessage.success('导入完成')
  } catch (error) {
    importResult.value = {
      success: false,
      message: '导入失败：' + error.message,
      successCount: 0,
      failCount: previewData.value.length,
      duration: 0
    }
    
    ElMessage.error('导入失败：' + error.message)
  } finally {
    importing.value = false
  }
}

// 重置导入
const resetImport = () => {
  currentStep.value = 0
  fileList.value = []
  selectedFile.value = null
  previewData.value = []
  validationErrors.value = []
  importResult.value = null
}

// 页面加载
onMounted(() => {
  // 初始化
})
</script>

<style scoped>
.user-import-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.import-steps {
  margin-bottom: 30px;
}

.step-content {
  min-height: 400px;
}

.upload-area {
  text-align: center;
  margin: 30px 0;
}

.template-download {
  margin: 30px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.template-download h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.template-download p {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}

.step-actions {
  text-align: center;
  margin-top: 30px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.preview-header h3 {
  margin: 0;
}

.preview-table {
  margin-bottom: 20px;
}

.validation-summary {
  margin-bottom: 20px;
}

.error-text {
  color: #f56c6c;
}

.success-text {
  color: #67c23a;
}

.confirm-content {
  text-align: center;
  padding: 40px 0;
}

.import-summary {
  text-align: left;
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.import-summary h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.import-summary p {
  margin: 8px 0;
  color: #606266;
}

.import-result {
  margin-top: 30px;
}

.result-details {
  text-align: left;
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.result-details p {
  margin: 8px 0;
  color: #606266;
}
</style> 