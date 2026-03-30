<template>
  <div style="padding: 20px;">
    <el-card style="max-width: 600px;">
      <template #header>
        <div class="card-header"><span>🌟 申请创建新社团</span></div>
      </template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="社团名称">
          <el-input v-model="form.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团简介">
          <el-input v-model="form.description" type="textarea" placeholder="请输入社团简介" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitApply">提交申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '../utils/request' // 🚩 必须有这一行！
import { ElMessage } from 'element-plus'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const form = ref({
  name: '',
  description: '',
  leaderId: user.userId // 🚩 这里的字段名必须和后端实体类一致
})

const submitApply = async () => {
  // 必须确保后端 @RequestMapping 是 /club
  const res = await request.post('/club/apply', form.value)
  if (res.code === 200) {
    ElMessage.success('提交成功，等待管理员审核')
    form.value.name = ''
    form.value.description = ''
  }
}
</script>