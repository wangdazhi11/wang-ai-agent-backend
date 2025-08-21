<template>
  <ChatRoom :chatApi="api" :sse="true" :chatId="chatId">
    <template #title>
      <div class="row-gap-8">
        <div class="avatar study">学</div>
        <div>
          <div class="card-title" style="margin:0">学习规划大师</div>
          <div class="text-dim" style="font-size:12px">个性化·系统化·可执行的学习方案</div>
        </div>
      </div>
    </template>
  </ChatRoom>
</template>

<script setup>
import { ref } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'

function genId() {
  return Math.random().toString(36).slice(2, 10)
}
const chatId = ref(genId())
// 使用相对路径，通过 Vite 代理转发到 http://localhost:8123
const api = '/api/ai/studyApp/chat/sse'
</script>