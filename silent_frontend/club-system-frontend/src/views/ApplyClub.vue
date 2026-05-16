<template>
  <div class="page-shell">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><CirclePlus /></el-icon>
            <div>
              <h2>申请创建新社团</h2>
              <p>填写社团名称和简介，提交后等待管理员审核。</p>
            </div>
          </div>
          <div class="header-actions">
            <el-tag type="info" effect="plain">普通成员入口</el-tag>
          </div>
        </div>
      </template>

      <div class="form-wrap">
        <el-form :model="form" label-width="100px" class="apply-form">
          <el-form-item label="社团名称">
            <el-input v-model="form.name" placeholder="请输入社团名称" clearable />
          </el-form-item>
          <el-form-item label="社团简介">
            <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请输入社团简介" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submitApply">
              提交申请
            </el-button>
            <el-button plain @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { CirclePlus } from '@element-plus/icons-vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const submitting = ref(false)
const form = ref({
  name: '',
  description: '',
  leaderId: user.userId
})

const resetForm = () => {
  form.value.name = ''
  form.value.description = ''
}

const submitApply = async () => {
  if (!form.value.name.trim()) {
    ElMessage.warning('请先填写社团名称')
    return
  }

  submitting.value = true
  try {
    const res = await request.post('/club/apply', form.value)
    if (res.code === 200) {
      ElMessage.success('提交成功，等待管理员审核')
      resetForm()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-shell {
  padding: 20px;
}

.page-card {
  max-width: 760px;
  margin: 0 auto;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(31, 45, 61, 0.06);
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
  background: linear-gradient(135deg, #13c2c2, #5cd9d5);
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

.form-wrap {
  padding: 8px 4px 4px;
}

.apply-form {
  max-width: 640px;
  margin: 0 auto;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 12px;
}

:deep(.el-button) {
  border-radius: 12px;
}

@media (max-width: 768px) {
  .page-card {
    max-width: 100%;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
