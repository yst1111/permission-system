<template>
  <div class="menu-container">
    <el-card>
      <!-- 操作按钮 -->
      <div class="operation-area">
        <el-button type="primary" @click="handleAdd">新增菜单</el-button>
        <el-button type="success" @click="handleExpand">展开/收起</el-button>
      </div>
      
      <!-- 菜单表格 -->
      <el-table
        :data="menuList"
        row-key="id"
        border
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
      >
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="menuCode" label="菜单编码" width="150" />
        <el-table-column prop="path" label="路由路径" width="200" />
        <el-table-column prop="component" label="组件路径" width="200" />
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="scope">
            <el-icon v-if="scope.row.icon">
              <component :is="scope.row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="menuType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.menuType === 1 ? 'primary' : 'success'">
              {{ scope.row.menuType === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleAdd(scope.row)">新增</el-button>
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 菜单表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="menuFormRef" :model="menuForm" :rules="menuRules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTreeData"
            :props="{ label: 'menuName', value: 'id' }"
            placeholder="请选择上级菜单"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单编码" prop="menuCode">
          <el-input v-model="menuForm.menuCode" placeholder="请输入菜单编码" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="menuForm.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="menuForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="menuForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="menuForm.menuType">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="menuForm.status">
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
const menuList = ref([])
const menuTreeData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const menuFormRef = ref()
const isExpandAll = ref(true)

const menuForm = reactive({
  id: null,
  parentId: 0,
  menuName: '',
  menuCode: '',
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  menuType: 1,
  status: 1
})

const menuRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  menuCode: [
    { required: true, message: '请输入菜单编码', trigger: 'blur' }
  ]
}

// 获取菜单树
const fetchMenuTree = async () => {
  loading.value = true
  try {
    const response = await request.get('/menu/tree')
    menuList.value = response.data.data || []
    menuTreeData.value = [{ id: 0, menuName: '顶级菜单' }, ...menuList.value]
  } catch (error) {
    console.error('获取菜单树失败:', error)
  } finally {
    loading.value = false
  }
}

// 方法
const handleAdd = (row) => {
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
  resetForm()
  if (row) {
    menuForm.parentId = row.id
  }
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜单'
  dialogVisible.value = true
  Object.assign(menuForm, row)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/menu/${row.id}`)
    ElMessage.success('删除成功')
    fetchMenuTree()
  } catch {
    // 用户取消
  }
}

const handleExpand = () => {
  isExpandAll.value = !isExpandAll.value
}

const handleSubmit = async () => {
  if (!menuFormRef.value) return
  
  try {
    await menuFormRef.value.validate()
    
    if (menuForm.id) {
      await request.put('/menu/update', menuForm)
    } else {
      await request.post('/menu/add', menuForm)
    }
    ElMessage.success(menuForm.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    resetForm()
    fetchMenuTree()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const resetForm = () => {
  Object.assign(menuForm, {
    id: null,
    parentId: 0,
    menuName: '',
    menuCode: '',
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    menuType: 1,
    status: 1
  })
  if (menuFormRef.value) {
    menuFormRef.value.resetFields()
  }
}

// 生命周期
onMounted(() => {
  fetchMenuTree()
})
</script>

<style scoped>
.menu-container {
  padding: 20px;
}

.operation-area {
  margin-bottom: 20px;
}
</style> 