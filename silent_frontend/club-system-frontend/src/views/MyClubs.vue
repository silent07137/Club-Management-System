<template>
  <div class="my-clubs-container">
    <h2 style="margin-bottom: 20px; color: #409EFF;">🚩 我加入的社团</h2>
    
    <el-row :gutter="20" v-if="myClubs.length > 0">
      <el-col :span="8" v-for="item in myClubs" :key="item.memberId">
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div class="card-header">
              <span>社团 ID: {{ item.clubId }}</span>
              <el-tag type="success">正式成员</el-tag>
            </div>
          </template>
          <p>角色类型：{{ item.roleType === 1 ? '社长' : '普通成员' }}</p>
          <p style="font-size: 12px; color: #999;">加入时间：{{ item.createTime }}</p>
          <div style="text-align: right;">
            <el-button type="primary" plain size="small">进入社团</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-else description="你还没有加入任何社团，快去广场看看吧！" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const myClubs = ref([])
const user = JSON.parse(localStorage.getItem('user') || '{}')

const loadMyClubs = async () => {
  const userId = user.id || user.userId
  if (!userId) return
  
  const res = await request.get('/club/member/my', { params: { userId } })
  if (res.code == 200) {
    myClubs.value = res.data
  }
}

onMounted(() => {
  loadMyClubs()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>