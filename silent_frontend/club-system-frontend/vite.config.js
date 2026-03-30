import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173, // 确保前端跑在这个端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 你后端的真实地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '') // 比如把 /api/user/login 替换成 /user/login 发给后端
      }
    }
  }
})