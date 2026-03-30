<template>
  <div style="padding: 20px;">
    <h2>👋 欢迎回来！</h2>
    <p style="color: #666; margin-bottom: 30px;">这是您的社团管理专属数据看板</p>

    <el-row :gutter="20">
      
      <el-col :span="8">
        <el-card shadow="hover" style="border-radius: 10px;">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <div style="color: #909399; font-size: 14px; margin-bottom: 10px;">注册成员总数</div>
              <div style="font-size: 32px; font-weight: bold; color: #409EFF;">
                {{ statsData.userCount }} <span style="font-size: 16px; color: #666;">人</span>
              </div>
            </div>
            <div style="font-size: 40px;">👥</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" style="border-radius: 10px;">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <div style="color: #909399; font-size: 14px; margin-bottom: 10px;">累计社团活动</div>
              <div style="font-size: 32px; font-weight: bold; color: #67C23A;">
                {{ statsData.activityCount }} <span style="font-size: 16px; color: #666;">场</span>
              </div>
            </div>
            <div style="font-size: 40px;">🎉</div>
          </div>
        </el-card>
      </el-col>

    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

// 存放统计数据的变量，默认给个 0
const statsData = ref({
  userCount: 0,
  activityCount: 0
})

// 向后端请求数据的方法
const loadStats = async () => {
    console.log("正在尝试获取统计数据...")
  try {
    const res = await request.get('/stats/info')
    console.log("后端返回的数据是：", res)
    if (res.code === 200) {
      statsData.value = res.data // 把后端查到的真实数字赋给页面
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 页面一加载，就去拉取数据
onMounted(() => {
  loadStats()
})
</script>