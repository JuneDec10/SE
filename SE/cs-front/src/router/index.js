import { createRouter, createWebHistory } from 'vue-router'
import search from '../views/Search.vue'
const routes = [
    {
        name: '登录',
        path: '/search',
        component: search,
    },
];

//这里不是通过new来创建，通过createRouter 方法创建，使用的模式不是通过mode来定义
const router = createRouter({
    routes,
    history: createWebHistory(),
})

export default router