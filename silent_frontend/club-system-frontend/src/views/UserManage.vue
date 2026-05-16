<template>
  <div class="page-shell">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><UserFilled /></el-icon>
            <div>
              <h2>成员管理中心</h2>
              <p>系统身份用于平台权限，业务身份用于社团角色识别。</p>
            </div>
          </div>
            <div class="header-actions">
              <el-button :loading="loading" @click="loadData">刷新成员</el-button>
            </div>
          </div>
        </template>

      <div class="toolbar-row">
        <el-input 
          v-model="searchName" 
          placeholder="请输入姓名搜索" 
          class="search-input"
          clearable 
          @clear="loadData" 
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadData">搜索成员</el-button>
      </div>

      <el-alert
        title="系统身份用于区分平台权限，业务身份用于区分社团参与角色。"
        type="info"
        show-icon
        :closable="false"
        style="margin-bottom: 16px;"
      />

      <el-table :data="userList" stripe style="width: 100%" border>
        <el-table-column prop="userId" width="100">
          <template #header>
            <span class="column-header"><el-icon><Tickets /></el-icon><span>系统ID</span></span>
          </template>
        </el-table-column>
        <el-table-column prop="studentId" width="150">
          <template #header>
            <span class="column-header"><el-icon><Reading /></el-icon><span>学号</span></span>
          </template>
        </el-table-column>
        <el-table-column prop="name">
          <template #header>
            <span class="column-header"><el-icon><UserFilled /></el-icon><span>真实姓名</span></span>
          </template>
        </el-table-column>
        <el-table-column width="140">
          <template #header>
            <span class="column-header"><el-icon><Stamp /></el-icon><span>系统身份</span></span>
          </template>
          <template #default="scope">
            <el-tag :type="getSystemRoleType(scope.row)">
              {{ getSystemRoleLabel(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column width="140">
          <template #header>
            <span class="column-header"><el-icon><Suitcase /></el-icon><span>业务身份</span></span>
          </template>
          <template #default="scope">
            <el-tag :type="getBusinessRoleType(scope.row)">
              {{ getBusinessRoleLabel(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column width="150">
          <template #header>
            <span class="column-header"><el-icon><Operation /></el-icon><span>操作</span></span>
          </template>
          <template #default="scope">
            <el-button
              size="small"
              link
              type="danger"
              :disabled="scope.row.globalRole === 0"
              @click="handleRemoveFromClubs(scope.row.userId)"
            >
              移出社团
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, UserFilled, Tickets, Reading, Stamp, Suitcase, Operation } from '@element-plus/icons-vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const userList = ref([])
const loading = ref(false)

const getSystemRoleLabel = (row) => {
  if (Number(row.globalRole) === 0) {
    return '超级管理员'
  }
  return '普通用户'
}

const getSystemRoleType = (row) => {
  return Number(row.globalRole) === 0 ? 'danger' : 'success'
}

const getBusinessRoleLabel = (row) => {
  if (row.role === 'ROLE_ADMIN') {
    return '平台管理员'
  }
  if (row.role === 'leader') {
    return '社长'
  }
  if (row.role === 'student') {
    return '普通成员'
  }
  return row.role || '未定义'
}

const getBusinessRoleType = (row) => {
  if (row.role === 'ROLE_ADMIN') {
    return 'danger'
  }
  if (row.role === 'leader') {
    return 'warning'
  }
  if (row.role === 'student') {
    return 'success'
  }
  return 'info'
}

// 获取用户列表数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/list', {
      params: { name: searchName.value }
    })
    if (res.code === 200) {
      userList.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取成员列表失败')
  } finally {
    loading.value = false
  }
}


const handleRemoveFromClubs = (id) => {
  if (!id) {
    ElMessage.error('糟糕！拿不到这个人的 ID，请检查绑定的字段名是不是写错了！')
    return
  }

  ElMessageBox.confirm('确定要将该用户移出所有社团吗？此操作不会删除账号！', '高危操作', {
    confirmButtonText: '确认移出',
    cancelButtonText: '手滑了',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await request.delete(`/user/remove-from-clubs/${id}`)
      if (res.code === 200) {
        ElMessage.success('已成功移出该用户的所有社团！')
        loadData()
      } else {
        ElMessage.error(res.message)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('移出失败，请稍后重试')
    }
  }).catch(() => {
    ElMessage.info('已取消移出')
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-shell {
  padding: 20px;
}

.page-card {
  border-radius: 14px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff, #79bbff);
  color: #fff;
  font-size: 22px;
  flex: 0 0 auto;
}

.page-title h2 {
  margin: 0 0 4px;
  color: #1f2d3d;
}

.page-title p {
  margin: 0;
  color: #606266;
}

.header-actions {
  display: flex;
  align-items: center;
}

.toolbar-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.search-input {
  width: 280px;
}

.column-header {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .page-header,
  .toolbar-row {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
