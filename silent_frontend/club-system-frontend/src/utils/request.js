import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 5000,
    withCredentials: true
})

request.interceptors.request.use(
    config => {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (user.userId || user.id) {
            config.headers['X-User-Id'] = user.userId || user.id
        }
        if (user.role) {
            config.headers['X-User-Role'] = user.role
        }
        return config
    },
    error => Promise.reject(error)
)

request.interceptors.response.use(
    response => {
        let res = response.data;
        if (res.code === 200) {
            return res;
        } else {
            ElMessage.error(res.message || '系统错误');
            return Promise.reject(res.message);
        }
    },
    error => {
        ElMessage.error('网络连接异常，请检查后端服务是否启动');
        return Promise.reject(error);
    }
)

export default request;
