<template>
  <div class="login-page">
    <div class="login-ambient login-ambient-one"></div>
    <div class="login-ambient login-ambient-two"></div>

    <el-card class="login-card" shadow="never">
      <div class="brand-line">
        <div class="brand-mark">SC</div>
        <div>
          <div class="brand-title">社团管理系统</div>
          <div class="brand-subtitle">Club Admin Console</div>
        </div>
      </div>

      <template #header>
        <div class="card-header">
          <div class="eyebrow">{{ isLogin ? '登录入口' : '注册入口' }}</div>
          <h2>{{ isLogin ? '智慧校园社团系统' : '欢迎注册新账号' }}</h2>
          <p>{{ isLogin ? '登录后进入统一的社团管理工作台。' : '填写信息后即可创建账号并进入系统。' }}</p>
        </div>
      </template>

      <el-form :model="form" label-position="top" class="login-form">
        <el-form-item label="学号">
          <el-input v-model="form.studentId" placeholder="请输入学号 (如: 2023001)" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item v-if="!isLogin" label="真实姓名">
          <el-input v-model="form.name" placeholder="请输入你的真实姓名" />
        </el-form-item>

        <el-form-item>
          <el-button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">
            {{ isLogin ? '立即登录' : '注册并登录' }}
          </el-button>
        </el-form-item>

        <div class="toggle-text">
          <span>{{ isLogin ? '还没有账号？' : '已经有账号？' }}</span>
          <el-link type="primary" @click="toggleMode">
            {{ isLogin ? '点击去注册' : '点击去登录' }}
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
const submitting = ref(false)

const form = reactive({
  studentId: '',
  password: '',
  name: ''
})

const toggleMode = () => {
  isLogin.value = !isLogin.value
  form.password = ''
  form.name = ''
}

const handleSubmit = async () => {
  if (!form.studentId || !form.password) {
    ElMessage.warning('学号和密码不能为空哦！')
    return
  }

  if (!isLogin.value && !form.name.trim()) {
    ElMessage.warning('请先填写真实姓名')
    return
  }

  submitting.value = true
  try {
    if (!isLogin.value) {
      await request.post('/user/register', form)
    }

    const loginRes = await request.post('/user/login', {
      studentId: form.studentId,
      password: form.password
    })

    if (loginRes.code === 200) {
      localStorage.setItem('user', JSON.stringify(loginRes.data))
      ElMessage.success(isLogin.value ? '欢迎回来' : '注册成功，已自动登录')
      router.replace('/home')
    }
  } catch (error) {
    ElMessage.error(typeof error === 'string' ? error : '请求失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(64, 158, 255, 0.12), transparent 30%),
    radial-gradient(circle at right 20%, rgba(103, 194, 58, 0.12), transparent 24%),
    linear-gradient(180deg, #f5f8fc 0%, #eef3f8 100%);
}

.login-ambient {
  position: absolute;
  border-radius: 50%;
  filter: blur(20px);
  pointer-events: none;
}

.login-ambient-one {
  width: 280px;
  height: 280px;
  background: rgba(64, 158, 255, 0.12);
  top: -80px;
  left: -60px;
}

.login-ambient-two {
  width: 220px;
  height: 220px;
  background: rgba(103, 194, 58, 0.12);
  bottom: -60px;
  right: -40px;
}

.login-card {
  width: min(100%, 440px);
  border-radius: 20px;
  box-shadow: 0 18px 50px rgba(31, 45, 61, 0.12);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.brand-line {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, #409eff, #79bbff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  letter-spacing: 0.6px;
  flex: 0 0 auto;
}

.brand-title {
  font-size: 16px;
  font-weight: 800;
  color: #1f2d3d;
}

.brand-subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.card-header {
  text-align: left;
}

.eyebrow {
  font-size: 12px;
  color: #409eff;
  font-weight: 700;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.card-header h2 {
  margin: 0 0 8px;
  color: #1f2d3d;
  font-size: 26px;
  line-height: 1.2;
}

.card-header p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.login-form {
  margin-top: 6px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  font-weight: 700;
}

.toggle-text {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 2px;
  color: #606266;
}

@media (max-width: 768px) {
  .login-page {
    padding: 16px;
  }

  .login-card {
    width: 100%;
  }

  .card-header h2 {
    font-size: 22px;
  }
}
</style>
