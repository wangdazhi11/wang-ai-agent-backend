<template>
  <ChatRoom :chatApi="api" :sse="true" :chatId="chatId">
    <template #title>
      <div class="row-gap-8">
        <div class="avatar manus">智</div>
        <div>
          <div class="card-title" style="margin:0">超级智能体</div>
          <div class="text-dim" style="font-size:12px">多工具协作 · 分步推理 · 自动化执行</div>
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
const api = '/api/ai/manus/chat'
</script>