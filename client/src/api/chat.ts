import api from './index'
import type { ApiResult } from '@/types'

export interface ConversationItem {
  id: number
  buyerId: number
  sellerId: number
  productId: number
  lastMessage: string
  lastMessageAt: string
  createdAt: string
}

export interface MessageItem {
  id: number
  conversationId: number
  senderId: number
  content: string
  isRead: number
  createdAt: string
}

export function getConversations() {
  return api.get<ApiResult<ConversationItem[]>>('/conversations')
}

export function createConversation(productId: number) {
  return api.post<ApiResult<ConversationItem>>('/conversations', { productId })
}

export function getMessages(conversationId: number) {
  return api.get<ApiResult<MessageItem[]>>(`/conversations/${conversationId}/messages`)
}

export function sendMessage(conversationId: number, content: string) {
  return api.post<ApiResult<MessageItem>>(`/conversations/${conversationId}/messages`, { content })
}
