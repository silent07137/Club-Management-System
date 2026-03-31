<template>
    <div class="club-square">
        <h2 style="margin-bottom: 20px; color: #409EFF;">🌟 社团广场</h2>

        <el-skeleton :loading="loading" animated :count="3">
            <template #template>
                <el-row :gutter="20">
                    <el-col :span="8" v-for="i in 3" :key="i">
                        <el-skeleton-item variant="rect" style="height: 200px; margin-bottom: 20px" />
                    </el-col>
                </el-row>
            </template>

            <el-row :gutter="20">
                <el-col :span="8" v-for="club in clubList" :key="club.clubId">
                    <el-card class="club-card" shadow="hover">
                        <template #header>
                            <div class="card-header">
                                <span class="club-name">{{ club.name }}</span>
                                <el-tag type="success" size="small">运行中</el-tag>
                            </div>
                        </template>
                        <div class="club-desc">
                            {{ club.description || '这个社团很神秘，还没有写简介哦~' }}
                        </div>
                        <div class="card-footer">
                            <el-button type="primary" icon="Plus" round @click="handleApply(club)">申请加入</el-button>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </el-skeleton>

        <el-empty v-if="!loading && clubList.length === 0" description="暂无社团信息" />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(true)
const clubList = ref([])
const user = JSON.parse(localStorage.getItem('user') || '{}')

const loadClubs = async () => {
    loading.value = true
    try {
        const res = await request.get('/club/list')
        if (res.code == 200) {
            clubList.value = res.data
            console.log("社团列表已加载：", clubList.value)
        } else {
            console.error("后端返回错误：", res.message || res.msg)
        }
    } catch (error) {
        console.error("接口请求失败：", error)
    } finally {
        loading.value = false
    }
}

const handleApply = (club) => {
    const userStore = JSON.parse(localStorage.getItem('user') || '{}')
    console.log("当前登录用户信息：", user)
    const finalUserId = userStore.userId || userStore.id
    if (!finalUserId) {
        ElMessage.error("用户信息已失效，请重新登录")
        return
    }
    ElMessageBox.confirm(`确定申请加入【${club.name}】吗？`, '提示', { type: 'info' })
        .then(async () => {
            const res = await request.post('/club/member/apply', {
                userId: finalUserId, 
                clubId: club.clubId
            })

            if (res.code == 200) {
                ElMessage.success("申请成功！")
            } else {
                ElMessage.warning(res.msg)
            }
        }).catch(() => { })
}

onMounted(() => { loadClubs() })
</script>

<style scoped>
.club-square {
    padding: 20px;
}

.club-card {
    margin-bottom: 20px;
    transition: 0.3s;
}

.club-card:hover {
    transform: translateY(-5px);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.club-name {
    font-size: 18px;
    font-weight: bold;
}

.club-desc {
    height: 60px;
    color: #666;
    font-size: 14px;
    overflow: hidden;
}

.card-footer {
    margin-top: 15px;
    text-align: right;
}
</style>