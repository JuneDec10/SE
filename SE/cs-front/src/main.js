import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import "element-plus/dist/index.css"
import "@/assets/icon/iconfont.css"
import Request from '@/utils/Request'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App);
app.use(router);
app.use(ElementPlus);
app.config.globalProperties.Request = Request;
app.config.silent = true;
for(const [key,component] of Object.entries(ElementPlusIconsVue)){
    app.component(key,component);
}
app.mount('#app')
