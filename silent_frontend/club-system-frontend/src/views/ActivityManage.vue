<template>
  <div style="padding: 20px;">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="display: flex; gap: 10px;">
            <el-input v-model="searchTitle" placeholder="请输入活动名称搜索" style="width: 250px" clearable @clear="loadData" />
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>

          <el-button type="primary" size="small" @click="handleAdd">新增活动</el-button>
        </div>
      </template>
      <el-table :data="activityList" stripe style="width: 100%">
        <el-table-column prop="activityId" label="ID" width="80" />
        <el-table-column prop="title" label="活动名称" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '进行中' : '筹备中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="handleDelete(scope.row.activityId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.activityId ? '编辑活动' : '新增活动'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="活动名称">
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="活动状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">进行中</el-radio>
            <el-radio :value="0">筹备中</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
const searchTitle = ref('')
const activityList = ref([])
const dialogVisible = ref(false)

const loadData = async () => {
  try {
    // Axios 的 GET 请求，参数要写在 params 里
    const res = await request.get('/activity/list', {
      params: {
        title: searchTitle.value // 把搜索框里的字传给后端
      }
    }) 
    if (res.code === 200) {
      activityList.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取活动列表失败')
  }
}

const handleDelete = (id) => {
  // 🚩 加这一句监控：看看点击时到底有没有拿到 ID
  console.log("👉 点击了删除按钮，当前行的 ID 是：", id)

  if (!id) {
    ElMessage.error('无法获取该数据的ID，请按 F12 检查字段名！')
    return
  }

  ElMessageBox.confirm('确定要删除这个活动吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await request.delete(`/activity/delete/${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功！')
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const form = reactive({
  title: '',
  location: '',
  status: 1,
  clubId: 1, // 默认挂在ID为1的社团下
  startTime: '2026-05-01 10:00:00', // 默认开始时间
  endTime: '2026-05-01 12:00:00'    // 默认结束时间
})
// 🚩 点击新增按钮，打开弹窗并清空表单
const handleAdd = () => {
  form.activityId = undefined // 🚩 关键：清空 ID，证明这是新数据
  form.title = ''
  form.location = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  // Object.assign 会把 row 里的数据复制到 form 里，这样弹窗一开就有数据了
  Object.assign(form, row)
  dialogVisible.value = true
}

// 🚩 点击确认保存，发送 POST 请求给后端
const handleSave = async () => {
  if (!form.title || !form.location) {
    ElMessage.warning('名称和地点不能为空哦！')
    return
  }

  try {
    let res;
    // 🚩 核心逻辑：判断表单里有没有 activityId
    if (form.activityId) {
      // 有 ID，说明是旧数据，发 PUT 请求去修改
      res = await request.put('/activity/update', form)
    } else {
      // 没 ID，说明是新数据，发 POST 请求去新增
      res = await request.post('/activity/add', form)
    }

    if (res.code === 200) {
      ElMessage.success(form.activityId ? '修改成功！' : '新增成功！')
      dialogVisible.value = false
      loadData() // 刷新表格
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
  }
}
// 页面加载时执行
onMounted(() => {
  loadData()
})
</script>