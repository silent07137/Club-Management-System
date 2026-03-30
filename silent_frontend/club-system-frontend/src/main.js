import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router' // 1. 必须引入路由

const app = createApp(App)
app.use(ElementPlus)
app.use(router) // 2. 必须挂载路由，没这一行就是空白！
app.mount('#app')