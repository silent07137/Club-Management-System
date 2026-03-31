<template>
  <div class="login-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h2>{{ isLogin ? '智慧校园社团系统 - 登录' : '欢迎注册新账号' }}</h2>
        </div>
      </template>

      <el-form :model="form" label-width="80px">
        <el-form-item label="学号">
          <el-input v-model="form.studentId" placeholder="请输入学号 (如: 2023001)"></el-input>
        </el-form-item>
        
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>

        <el-form-item v-if="!isLogin" label="真实姓名">
          <el-input v-model="form.name" placeholder="请输入你的真实姓名"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" style="width: 100%;">
            {{ isLogin ? '立 即 登 录' : '注 册 并 登 录' }}
          </el-button>
        </el-form-item>
        
        <div class="toggle-text">
          <el-link type="primary" @click="isLogin = !isLogin">
            {{ isLogin ? '没有账号？点击去注册' : '已有账号？点击去登录' }}
          </el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue' 
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const isLogin = ref(true)

const form = reactive({
  studentId: '',
  password: '',
  name: ''
})

const handleSubmit = async () => {
  if (!form.studentId || !form.password) {
    ElMessage.warning('学号和密码不能为空哦！')
    return
  }

  try {
    const url = isLogin.value ? '/user/login' : '/user/register'
    const res = await request.post(url, form)
    
    if (res.code === 200) {
      localStorage.setItem("token", res.data.token)
      localStorage.setItem("user", JSON.stringify(res.data))
      ElMessage.success("欢迎回来")
      router.push('/home') 
    }
  } catch (error) {
    console.log("请求失败", error)
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f3f4f6;
}
.box-card { width: 400px; }
.card-header h2 { margin: 0; text-align: center; color: #409EFF; }
.toggle-text { text-align: center; margin-top: 10px; }
</style>