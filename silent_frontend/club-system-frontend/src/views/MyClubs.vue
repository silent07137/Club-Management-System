<template>
    <div class="my-clubs-container">
        <h2 style="margin-bottom: 20px; color: #409EFF;">🚩 我加入的社团</h2>

        <el-row :gutter="20" v-if="myClubs.length > 0">
            <el-col :span="8" v-for="item in myClubs" :key="item.memberId">
                <el-card shadow="hover" style="margin-bottom: 20px;">
                    <template #header>
                        <div class="card-header">
                            <span>社团 ID: {{ item.clubId }}</span>
                            <span style="font-weight: bold; font-size: 18px;">{{ item.clubName }}</span>
                            <el-tag type="success">正式成员</el-tag>
                        </div>
                    </template>
                    <p>角色类型：{{ item.roleType === 1 ? '社长' : '普通成员' }}</p>
                    <p style="font-size: 12px; color: #999;">加入时间：{{ item.createTime }}</p>
                    <div style="text-align: right;">
                        <el-button type="primary" plain size="small"
                            @click="router.push('/club-detail/' + item.clubId)">
                            进入社团
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDeleteClub(item.clubId)">
                            删除
                        </el-button>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-empty v-else description="你还没有加入任何社团，快去广场看看吧！" />
    </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
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
const goDetail = (id) => {
    // 使用模板字符串把 ID 塞进路径里
    router.push(`/home/club-detail/${id}`)
}

onMounted(() => {
    loadMyClubs()
})

const handleDeleteClub = (clubId) => {
    ElMessageBox.confirm(
        '确定要解散该社团吗？所有成员将被移出，此操作不可恢复！',
        '高危操作确认',
        {
            confirmButtonText: '确定解散',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            // 调用后端的删除接口
            const res = await request.delete(`/club/delete/${clubId}`);
            if (res.code === 200) { // 根据你 Result 类的成功状态码调整
                ElMessage.success('社团已成功解散');
                // TODO: 重新调用获取列表的接口刷新页面数据
                loadMyClubs(); 
            } else {
                ElMessage.error(res.msg || '删除失败');
            }
        } catch (error) {
            console.error('请求出错:', error);
        }
    }).catch(() => {
        ElMessage.info('已取消删除');
    });
}

</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>