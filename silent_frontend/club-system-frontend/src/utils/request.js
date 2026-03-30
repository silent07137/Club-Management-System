import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建一个 axios 实例
const request = axios.create({
    baseURL: '/api', // 注意这里：配合上面的 Vite 代理，所有的请求都会自动加上 /api 前缀
    timeout: 5000
})

// 响应拦截器：后端返回数据后，这里会先拦截下来处理
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 如果后端返回 code 是 200，说明业务成功，直接把数据剥离出来给页面
        if (res.code === 200) {
            return res;
        } else {
            // 如果后端返回 500（比如账号密码错误），直接弹出咱们在后端写的 message
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