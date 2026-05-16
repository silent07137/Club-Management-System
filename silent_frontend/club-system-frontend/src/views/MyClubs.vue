<template>
  <div class="page-shell my-clubs-container">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <el-icon class="title-icon"><UserFilled /></el-icon>
            <div>
              <h2>我加入的社团</h2>
              <p>这里集中展示你已加入的社团和对应身份。</p>
            </div>
          </div>
          <div class="header-actions">
            <el-badge :value="myClubs.length" :hidden="myClubs.length === 0" type="primary">
              <el-button :loading="loading" @click="loadMyClubs">
                <el-icon style="margin-right: 4px;"><Refresh /></el-icon>
                刷新
              </el-button>
            </el-badge>
          </div>
        </div>
      </template>

      <el-row :gutter="20" v-if="myClubs.length > 0">
        <el-col :xs="24" :sm="12" :lg="8" v-for="item in myClubs" :key="item.memberId">
          <el-card class="club-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="card-title">
                  <div class="club-name">{{ item.clubName }}</div>
                  <div class="club-meta">
                    <span><el-icon><Tickets /></el-icon> 社团 ID：{{ item.clubId }}</span>
                  </div>
                </div>
                <div class="card-status">
                  <el-tag type="success" size="small" effect="plain">已加入</el-tag>
                  <el-tag :type="item.roleType === 1 ? 'danger' : 'warning'" size="small">
                    {{ item.roleType === 1 ? '社长' : '普通成员' }}
                  </el-tag>
                </div>
              </div>
            </template>

            <div class="club-info">
              <p>
                <el-icon class="info-icon"><Stamp /></el-icon>
                当前身份：{{ item.roleType === 1 ? '社长' : '普通成员' }}
              </p>
              <p>
                <el-icon class="info-icon"><Clock /></el-icon>
                加入时间：{{ item.createTime }}
              </p>
            </div>

            <div class="card-footer">
              <el-button type="primary" plain size="small" @click="router.push('/club-detail/' + item.clubId)">
                进入社团
              </el-button>
              <el-button
                v-if="item.roleType !== 1"
                type="warning"
                size="small"
                plain
                @click="handleQuitClub(item.clubId)"
              >
                退出社团
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-else description="你还没有加入任何社团，快去广场看看吧！" />
    </el-card>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, Refresh, Tickets, Stamp, Clock } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const myClubs = ref([])
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')

const loadMyClubs = async () => {
    const userId = user.id || user.userId
    if (!userId) return

    loading.value = true
    try {
        const res = await request.get('/club/member/my', { params: { userId } })
        if (res.code == 200) {
            myClubs.value = res.data
        }
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    loadMyClubs()
})

const handleQuitClub = (clubId) => {
    ElMessageBox.confirm(
        '确定要退出该社团吗？',
        '退出确认',
        {
            confirmButtonText: '确定退出',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            const res = await request.delete('/club/member/quit', {
                params: { clubId }
            });
            if (res.code === 200) {
                ElMessage.success(res.message || '已退出社团');
                loadMyClubs();
            } else {
                ElMessage.error(res.message || '退出失败');
            }
        } catch (error) {
            console.error('请求出错:', error);
        }
    }).catch(() => {
        ElMessage.info('已取消退出');
    });
}

</script>

<style scoped>
.page-shell {
    padding: 20px;
}

.page-card {
    border-radius: 18px;
    box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
    margin-bottom: 4px;
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
    font-size: 20px;
    line-height: 1.2;
}

.page-title p {
    margin: 0;
    color: #606266;
    line-height: 1.5;
}

.header-actions {
    display: flex;
    align-items: center;
}

.club-card {
    margin-bottom: 20px;
    border-radius: 18px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
}

.card-status {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-end;
    gap: 8px;
}

.card-status :deep(.el-tag),
.card-header :deep(.el-tag) {
    border-radius: 999px;
    height: 28px;
    line-height: 26px;
    padding: 0 12px;
}

.card-title {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.club-name {
    font-weight: 700;
    font-size: 17px;
    color: #303133;
    line-height: 1.25;
}

.club-meta {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    color: #909399;
    font-size: 12px;
    line-height: 1.4;
}

.club-info p {
    margin: 0 0 10px;
    color: #606266;
    display: flex;
    align-items: center;
    gap: 6px;
    line-height: 1.6;
}

.info-icon {
    color: #409eff;
}

.card-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 16px;
}

@media (max-width: 768px) {
    .page-header {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
