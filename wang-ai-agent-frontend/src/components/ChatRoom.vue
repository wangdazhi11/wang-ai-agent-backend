<template>
	<div class="chat-room card chat-wrapper">
		<header class="chat-head">
			<slot name="title">
				<div class="row-gap-8"><div class="avatar ai">AI</div><div><div class="card-title" style="margin:0">智能对话</div><div class="text-dim" style="font-size:12px">流式响应 · 实时生成</div></div></div>
			</slot>
		</header>
		<section class="chat-history" ref="historyRef">
			<div v-for="(msg, idx) in messages" :key="idx" :class="['bubble-row', msg.role]">
				<div v-if="msg.role==='ai'" class="avatar ai small">AI</div>
				<div v-if="msg.role==='user'" class="avatar user small">我</div>
				<div class="bubble"><pre class="bubble-text">{{ msg.content }}</pre></div>
			</div>
		</section>
		<footer class="chat-input">
			<input v-model="input" @keyup.enter="send" placeholder="请输入内容..." />
			<button class="btn" @click="send">发送</button>
		</footer>
	</div>
</template>

<script setup>
import { ref, reactive, nextTick, onBeforeUnmount } from 'vue'

const props = defineProps({
	chatApi: { type: String, required: true },
	sse: { type: Boolean, default: true },
	chatId: { type: String, required: false }
})

const messages = ref([])
const input = ref('')
const historyRef = ref(null)
let eventSource = null
let fetchAbortController = null
let fallbackTimer = null
let receivedAnyChunk = false

function scrollToBottom() {
	nextTick(() => {
		if (historyRef.value) {
			historyRef.value.scrollTop = historyRef.value.scrollHeight
		}
	})
}

function buildFinalUrl(text) {
	const url = new URL(props.chatApi, window.location.origin)
	url.searchParams.set('message', text)
	if (props.chatId) url.searchParams.set('chatId', props.chatId)
	const useAbsolute = props.chatApi.startsWith('http://') || props.chatApi.startsWith('https://')
	return useAbsolute ? `${props.chatApi}?${url.searchParams.toString()}` : `${url.pathname}?${url.searchParams.toString()}`
}

function tryAppendParsed(aiMsg, raw) {
	if (!raw) return
	let appended = false
	try {
		const trimmed = raw.trim()
		if (!trimmed) return
		if (trimmed.startsWith('{') || trimmed.startsWith('[')) {
			try {
				const payload = JSON.parse(trimmed)
				const candidates = [payload?.message, payload?.data, payload?.text, payload?.content, payload?.choices?.[0]?.delta?.content, payload?.choices?.[0]?.message?.content]
				const text = candidates.find(v => typeof v === 'string')
				if (text) { aiMsg.content += text; appended = true }
			} catch {}
		}
		if (!appended && trimmed.startsWith('message:')) { aiMsg.content += trimmed.slice(8).trimStart(); appended = true }
		if (!appended && trimmed.startsWith('data:')) { aiMsg.content += trimmed.slice(5).trimStart(); appended = true }
		if (!appended) { aiMsg.content += trimmed }
	} finally { scrollToBottom() }
}

async function startFetchSSE(finalUrl, aiMsg) {
	try {
		fetchAbortController = new AbortController()
		const res = await fetch(finalUrl, { method:'GET', headers:{ 'Accept':'text/event-stream','Cache-Control':'no-cache','Connection':'keep-alive' }, signal: fetchAbortController.signal, credentials:'same-origin' })
		if (!res.ok || !res.body) { aiMsg.content = `连接失败：${res.status} ${res.statusText}`; scrollToBottom(); return }
		const reader = res.body.getReader(); const decoder = new TextDecoder('utf-8'); let buffer = ''
		while (true) { const { value, done } = await reader.read(); if (done) break; receivedAnyChunk = true; buffer += decoder.decode(value, { stream:true }); const parts = buffer.split(/\r?\n\r?\n/); buffer = parts.pop() || ''; for (const part of parts){ const lines = part.split(/\r?\n/); let known=false; for(const line of lines){ if(line.startsWith('data:')||line.startsWith('message:')){ known=true; tryAppendParsed(aiMsg,line) } } if(!known) tryAppendParsed(aiMsg,part) } }
		const rest = buffer.trim(); if (rest) tryAppendParsed(aiMsg, rest)
	} catch (err) { if (!aiMsg.content) aiMsg.content = '连接失败：SSE 回退也未收到数据'; scrollToBottom() }
}

function send() {
	const text = input.value.trim(); if (!text) return
	messages.value.push({ role:'user', content: text })
	input.value = ''; scrollToBottom()
	if (!props.sse) return
	if (eventSource) try{ eventSource.close() }catch{}; if (fetchAbortController) try{ fetchAbortController.abort() }catch{}; if (fallbackTimer) clearTimeout(fallbackTimer)
	receivedAnyChunk = false
	const finalUrl = buildFinalUrl(text)
	const aiMsg = reactive({ role:'ai', content:'' }); messages.value.push(aiMsg)
	const handleMessage = (e)=>{ receivedAnyChunk = true; if (e.data === '[DONE]'){ try{eventSource.close()}catch{}; return } tryAppendParsed(aiMsg, e.data) }
	try{ eventSource = new EventSource(finalUrl) }catch(e){ startFetchSSE(finalUrl, aiMsg); return }
	eventSource.onmessage = handleMessage; eventSource.addEventListener('message', handleMessage)
	eventSource.onerror = ()=>{ try{eventSource.close()}catch{}; if (!receivedAnyChunk) startFetchSSE(finalUrl, aiMsg) }
	fallbackTimer = setTimeout(()=>{ if(!receivedAnyChunk){ try{eventSource.close()}catch{}; startFetchSSE(finalUrl, aiMsg) } }, 1500)
}

onBeforeUnmount(()=>{ if (eventSource) try{eventSource.close()}catch{}; if(fetchAbortController) try{fetchAbortController.abort()}catch{}; if (fallbackTimer) clearTimeout(fallbackTimer) })
</script>

<style scoped>
.chat-room{ display:flex; flex-direction:column; border:1px solid var(--border); border-radius:16px; background: linear-gradient(180deg, var(--card), var(--card-2)); box-shadow: var(--shadow); overflow:hidden }
.chat-head{ padding:14px 16px; border-bottom:1px solid var(--border) }
.chat-history{ flex:1; overflow:auto; padding:16px }
.chat-input{ border-top:1px solid var(--border); padding:12px; display:flex; gap:10px; background: rgba(255,255,255,.7) }
.chat-input input{ flex:1; padding:12px 14px; border-radius:10px; border:1px solid var(--border); background:#fff; color:var(--text) }

.bubble-row{ display:flex; gap:10px; align-items:flex-start; margin:12px 0 }
.bubble-row.user{ flex-direction: row-reverse }
.avatar.small{ width:32px; height:32px; font-size:12px }

.bubble{ max-width:min(78%, 720px); border:1px solid var(--border); background: #f5f7fb; padding:10px 12px; border-radius:12px; }
.bubble-row.user .bubble{ background: rgba(47,106,255,.10); border-color: rgba(47,106,255,.30) }
.bubble-text{ margin:0; white-space:pre-wrap; word-break:break-word; font-family: inherit; font-size:14px; line-height:1.7; text-align:left; color:var(--text) }

@media (max-width: 768px){ .bubble{ max-width: 86% } }
</style>