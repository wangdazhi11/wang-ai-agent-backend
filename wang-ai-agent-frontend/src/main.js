import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './pages/Home.vue'
import StudyApp from './pages/StudyApp.vue'
import ManusApp from './pages/ManusApp.vue'
import './styles/theme.css'

const routes = [
  { path: '/', component: Home },
  { path: '/study', component: StudyApp },
  { path: '/manus', component: ManusApp }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)

// 卡片高光跟随鼠标
window.addEventListener('mousemove', (e) => {
  document.querySelectorAll('.card').forEach((el) => {
    const rect = el.getBoundingClientRect()
    const x = (e.clientX - rect.left) / rect.width
    el.style.setProperty('--mx', `${x*100}%`)
  })
})

app.mount('#app')