<script setup lang="ts">
import { onMounted, ref, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getConversations, getMessages, sendMessage, type ConversationItem, type MessageItem } from '@/api/chat'
import { ElMessage } from 'element-plus'

const route = useRoute()
const conversations = ref<ConversationItem[]>([])
const messages = ref<MessageItem[]>([])
const activeConv = ref<number | null>(null)
const inputText = ref('')

onMounted(async () => {
  try { const r = await getConversations(); conversations.value = r.data.data } catch {}
  const cid = Number(route.params.id)
  if (cid) { activeConv.value = cid; loadMessages(cid) }
})

async function loadMessages(cid: number) {
  try { const r = await getMessages(cid); messages.value = r.data.data; nextTick(scrollBottom) } catch {}
}

async function onSend() {
  if (!inputText.value.trim() || !activeConv.value) return
  try {
    await sendMessage(activeConv.value, inputText.value)
    inputText.value = ''
    loadMessages(activeConv.value)
  } catch { ElMessage.error('发送失败') }
}

function scrollBottom() {
  const el = document.querySelector('.msg-list')
  if (el) el.scrollTop = el.scrollHeight
}
</script>

<template>
  <div style="max-width:800px;margin:0 auto;padding:20px;display:flex;gap:16px">
    <div style="width:250px;border-right:1px solid #eee">
      <h3>会话</h3>
      <el-empty v-if="!conversations.length" description="暂无会话" />
      <div v-for="c in conversations" :key="c.id"
        :style="{padding:'8px',cursor:'pointer',background:activeConv===c.id?'#e6f7ff':'transparent'}"
        @click="activeConv=c.id;loadMessages(c.id)">
        {{ c.lastMessage || '(新会话)' }}
      </div>
    </div>
    <div style="flex:1">
      <el-empty v-if="!activeConv" description="选择一个会话" />
      <template v-if="activeConv">
        <div class="msg-list" style="height:400px;overflow-y:auto;border:1px solid #eee;padding:12px;margin-bottom:12px">
          <div v-for="m in messages" :key="m.id" style="margin-bottom:8px">
            <strong>#{{ m.senderId }}:</strong> {{ m.content }}
          </div>
        </div>
        <div style="display:flex;gap:8px">
          <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="onSend" />
          <el-button type="primary" @click="onSend">发送</el-button>
        </div>
      </template>
    </div>
  </div>
</template>
