<template>
  <div class="role-container">
    <el-card>
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="角色名称">
            <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
          </el-form-item>
          <el-form-item label="角色编码">
            <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 操作按钮 -->
      <div class="operation-area">
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
      </div>
      
      <!-- 表格 -->
      <el-table :data="roleList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="handlePermission(scope.row)">权限</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 响应式数据
const searchForm = reactive({
  roleName: '',
  roleCode: '',
  status: ''
})

const roleList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleFormRef = ref()

const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ]
}

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取角色列表
const fetchRoleList = async () => {
  loading.value = true
  try {
    const response = await request.get('/role/list', { params: searchForm })
    roleList.value = response.data.data || []
    pagination.total = roleList.value.length
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 方法
const handleSearch = () => {
  fetchRoleList()
}

const resetSearch = () => {
  Object.assign(searchForm, {
    roleName: '',
    roleCode: '',
    status: ''
  })
  fetchRoleList()
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
  Object.assign(roleForm, row)
}

const handlePermission = (row) => {
  ElMessage.info('权限管理功能开发中...')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/role/${row.id}`)
    ElMessage.success('删除成功')
    fetchRoleList()
  } catch {
    // 用户取消
  }
}

const handleSubmit = async () => {
  if (!roleFormRef.value) return
  
  try {
    await roleFormRef.value.validate()
    
    if (roleForm.id) {
      await request.put('/role/update', roleForm)
    } else {
      await request.post('/role/add', roleForm)
    }
    ElMessage.success(roleForm.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    resetForm()
    fetchRoleList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const resetForm = () => {
  Object.assign(roleForm, {
    id: null,
    roleName: '',
    roleCode: '',
    description: '',
    status: 1
  })
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchRoleList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchRoleList()
}

// 生命周期
onMounted(() => {
  fetchRoleList()
})
</script>

<style scoped>
.role-container {
  padding: 20px;
}

.search-area {
  margin-bottom: 20px;
}

.operation-area {
  margin-bottom: 20px;
}

.pagination-area {
  margin-top: 20px;
  text-align: right;
}
</style> 