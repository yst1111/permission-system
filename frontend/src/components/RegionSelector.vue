<template>
  <div class="region-selector">
    <!-- 级联选择器 -->
    <el-cascader
      v-if="type === 'cascader'"
      v-model="selectedValue"
      :options="regionOptions"
      :props="cascaderProps"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :size="size"
      :show-all-levels="showAllLevels"
      :separator="separator"
      @change="handleChange"
      style="width: 100%"
    />
    
    <!-- 树形选择器 -->
    <el-tree-select
      v-else-if="type === 'tree'"
      v-model="selectedValue"
      :data="regionTreeData"
      :props="treeProps"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :size="size"
      :check-strictly="checkStrictly"
      :multiple="multiple"
      @change="handleChange"
      style="width: 100%"
    />
    
    <!-- 下拉选择器 -->
    <el-select
      v-else
      v-model="selectedValue"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :size="size"
      :multiple="multiple"
      :filterable="filterable"
      @change="handleChange"
      style="width: 100%"
    >
      <el-option
        v-for="item in regionOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 定义组件属性
const props = defineProps({
  // 选择器类型：cascader(级联), tree(树形), select(下拉)
  type: {
    type: String,
    default: 'cascader',
    validator: (value) => ['cascader', 'tree', 'select'].includes(value)
  },
  // 选中的值
  modelValue: {
    type: [String, Number, Array],
    default: ''
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择地区'
  },
  // 是否可清空
  clearable: {
    type: Boolean,
    default: true
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // 是否显示完整路径
  showAllLevels: {
    type: Boolean,
    default: true
  },
  // 分隔符
  separator: {
    type: String,
    default: ' / '
  },
  // 是否严格模式
  checkStrictly: {
    type: Boolean,
    default: false
  },
  // 是否多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 是否可搜索
  filterable: {
    type: Boolean,
    default: true
  },
  // 地区级别限制
  level: {
    type: Number,
    default: 3,
    validator: (value) => [1, 2, 3].includes(value)
  },
  // 父级地区编码
  parentCode: {
    type: String,
    default: ''
  }
})

// 定义事件
const emit = defineEmits(['update:modelValue', 'change'])

// 响应式数据
const selectedValue = ref(props.modelValue)
const regionOptions = ref([])
const regionTreeData = ref([])
const loading = ref(false)

// 级联选择器配置
const cascaderProps = reactive({
  value: 'regionCode',
  label: 'regionName',
  children: 'children',
  checkStrictly: false,
  emitPath: true
})

// 树形选择器配置
const treeProps = reactive({
  label: 'regionName',
  value: 'regionCode',
  children: 'children'
})

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  selectedValue.value = newVal
})

// 监听选中值变化
watch(selectedValue, (newVal) => {
  emit('update:modelValue', newVal)
})

// 获取地区数据
const fetchRegionData = async () => {
  loading.value = true
  try {
    let response
    if (props.parentCode) {
      // 获取指定父级下的地区
      response = await request.get(`/api/region/children/${props.parentCode}`)
    } else {
      // 获取完整地区树
      response = await request.get('/api/region/tree')
    }
    
    const data = response.data.data || []
    
    if (props.type === 'cascader') {
      regionOptions.value = buildCascaderOptions(data)
    } else if (props.type === 'tree') {
      regionTreeData.value = data
    } else {
      regionOptions.value = buildSelectOptions(data)
    }
  } catch (error) {
    console.error('获取地区数据失败:', error)
    ElMessage.error('获取地区数据失败')
  } finally {
    loading.value = false
  }
}

// 构建级联选择器选项
const buildCascaderOptions = (regions) => {
  return regions.map(region => ({
    regionCode: region.regionCode,
    regionName: region.regionName,
    level: region.level,
    children: region.children && region.children.length > 0 
      ? buildCascaderOptions(region.children)
      : undefined
  }))
}

// 构建下拉选择器选项
const buildSelectOptions = (regions) => {
  const options = []
  
  const addOptions = (items, prefix = '') => {
    items.forEach(item => {
      const label = prefix ? `${prefix} / ${item.regionName}` : item.regionName
      options.push({
        value: item.regionCode,
        label: label,
        level: item.level
      })
      
      if (item.children && item.children.length > 0) {
        addOptions(item.children, label)
      }
    })
  }
  
  addOptions(regions)
  return options
}

// 处理选择变化
const handleChange = (value) => {
  emit('change', value)
}

// 获取选中地区的完整路径
const getSelectedPath = () => {
  if (!selectedValue.value) return []
  
  if (props.type === 'cascader') {
    return selectedValue.value
  } else if (props.type === 'tree') {
    // 对于树形选择器，需要根据选中的ID构建路径
    return buildPathFromId(selectedValue.value, regionTreeData.value)
  } else {
    // 对于下拉选择器，返回选中的值
    return selectedValue.value
  }
}

// 根据ID构建路径
const buildPathFromId = (id, regions) => {
  for (const region of regions) {
    if (region.id === id) {
      return [region.id]
    }
    if (region.children) {
      const childPath = buildPathFromId(id, region.children)
      if (childPath.length > 0) {
        return [region.id, ...childPath]
      }
    }
  }
  return []
}

// 暴露方法给父组件
defineExpose({
  getSelectedPath,
  refresh: fetchRegionData
})

// 生命周期
onMounted(() => {
  fetchRegionData()
})
</script>

<style scoped>
.region-selector {
  width: 100%;
}

/* 级联选择器样式 */
.el-cascader {
  width: 100%;
}

/* 树形选择器样式 */
.el-tree-select {
  width: 100%;
}

/* 下拉选择器样式 */
.el-select {
  width: 100%;
}
</style> 