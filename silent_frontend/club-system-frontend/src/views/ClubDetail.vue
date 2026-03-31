<template>
    <div class="club-detail-container" v-loading="loading">
        <div class="header-box">
            <el-page-header @back="goBack">
                <template #content>
                    <span class="header-title"> {{ club.name }} - 社团主页 </span>
                </template>
            </el-page-header>
        </div>

        <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="8">
                <el-card shadow="always" class="info-card">
                    <div class="club-logo">
                        <el-avatar :size="100"
                            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                    </div>
                    <h2 class="text-center">{{ club.name }}</h2>
                    <el-divider />
                    <div class="detail-item">
                        <el-icon>
                            <InfoFilled />
                        </el-icon>
                        <span class="label">社团简介：</span>
                        <p class="content">{{ club.description || '这个社团很神秘，还没有简介...' }}</p>
                    </div>
                    <div class="detail-item">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        <span class="label">成立时间：</span>
                        <span class="content">{{ club.createTime || '2024-01-01' }}</span>
                    </div>
                    <div class="detail-item">
                        <el-icon>
                            <User />
                        </el-icon>
                        <span class="label">当前身份：</span>
                        <el-tag type="success">正式成员</el-tag>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="16">
                <el-tabs type="border-card">
                    <el-tab-pane label="📢 最新公告">
                        <el-timeline>
                            <el-timeline-item timestamp="2024-03-30" placement="top">
                                <el-card>
                                    <h4>本周社团例会通知</h4>
                                    <p>请全体成员于周五下午 4 点在社团活动教室集合。</p>
                                </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="2024-03-25" placement="top">
                                <el-card>
                                    <h4>新成员欢迎仪式</h4>
                                    <p>欢迎所有新加入的小伙伴！</p>
                                </el-card>
                            </el-timeline-item>
                        </el-timeline>
                    </el-tab-pane>
                    <el-tab-pane label="🖼️ 社团相册">
                        <el-empty description="暂无照片" />
                    </el-tab-pane>
                    <el-tab-pane label="👥 成员列表">
                        <p>成员功能正在开发中...</p>
                    </el-tab-pane>
                </el-tabs>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import { InfoFilled, Calendar, User } from '@element-plus/icons-vue'

const route = useRoute()   // 获取路由参数对象
const router = useRouter() // 执行路由跳转对象

const club = ref({})
const loading = ref(false)

// 🚩 核心逻辑：加载社团详情
const loadClubDetail = async () => {
    loading.value = true
    // 从动态路由路径 club-detail/:id 中拿到 id
    const clubId = route.params.id
    console.log("准备请求的ID是:", clubId)
    try {
        const res = await request.get(`/club/${clubId}`)
        if (res.code == 200) {
            club.value = res.data
        }
    } finally {
        loading.value = false
    }
}

const goBack = () => {
    router.back()
}

onMounted(() => {
    loadClubDetail()
})
</script>

<style scoped>
.header-box {
    background: #fff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-title {
    font-weight: bold;
    font-size: 18px;
}

.info-card {
    border-radius: 12px;
}

.club-logo {
    text-align: center;
    padding: 20px 0;
}

.text-center {
    text-align: center;
    margin-top: 10px;
    color: #303133;
}

.detail-item {
    margin-bottom: 20px;
    font-size: 14px;
}

.label {
    font-weight: bold;
    margin-left: 8px;
    color: #606266;
}

.content {
    margin-top: 8px;
    color: #909399;
    line-height: 1.6;
}
</style>