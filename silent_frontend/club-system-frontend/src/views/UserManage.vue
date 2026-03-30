<template>
  <div style="padding: 20px;">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="display: flex; gap: 10px;">
            <el-input 
              v-model="searchName" 
              placeholder="请输入姓名搜索" 
              style="width: 250px"
              clearable 
              @clear="loadData" 
            />
            <el-button type="primary" @click="loadData">搜索成员</el-button>
          </div>
          <span style="font-weight: bold; color: #409EFF;">👥 成员管理中心</span>
        </div>
      </template>

      <el-table :data="userList" stripe style="width: 100%" border>
        <el-table-column prop="userId" label="系统ID" width="80" />
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="name" label="真实姓名" />
        <el-table-column label="角色身份" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.studentId === 'admin' ? 'danger' : 'success'">
              {{ scope.row.studentId === 'admin' ? '管理员' : '普通成员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" link type="danger" @click="handleDelete(scope.row.userId)">踢出社团</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const userList = ref([])

// 获取用户列表数据
const loadData = async () => {
  try {
    const res = await request.get('/user/list', {
      params: { name: searchName.value }
    })
    if (res.code === 200) {
      userList.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取成员列表失败')
  }
}


const handleDelete = (id) => {
  // 🚩 加一行打印，看看到底传进来了什么
  console.log("👉 点击了踢出按钮，传进来的 ID 是：", id)
  
  // 🚩 把悄悄退出改成大声报错
  if (!id) {
    ElMessage.error('糟糕！拿不到这个人的 ID，请检查绑定的字段名是不是写错了！')
    return
  }
  // 弹窗再次确认，防止手滑踢错人
  ElMessageBox.confirm('确定要将该成员踢出社团吗？此操作不可恢复！', '高危操作', {
    confirmButtonText: '狠心踢出',
    cancelButtonText: '手滑了',
    type: 'warning',
  }).then(async () => {
    try {
      // 🚩 发送 DELETE 请求给后端的 /user/delete/{id}
      const res = await request.delete(`/user/delete/${id}`)
      if (res.code === 200) {
        ElMessage.success('已成功踢出该成员！')
        loadData() // 踢完人之后，重新向后端要一次数据，刷新表格
      } else {
        ElMessage.error(res.message)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('系统开小差了')
    }
  }).catch(() => {
    ElMessage.info('已取消踢出')
  })
}

// 页面一加载就去查数据
onMounted(() => {
  loadData()
})
</script>