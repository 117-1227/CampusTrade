package com.campustrade.service;

import com.campustrade.entity.Conversation;
import com.campustrade.entity.Message;

import java.util.List;

public interface ConversationService {
    Conversation getOrCreate(Long buyerId, Long productId);
    List<Conversation> listByUser(Long userId);
    Message sendMessage(Long conversationId, Long senderId, String content);
    List<Message> getMessages(Long conversationId, Long userId);
    void checkParticipant(Long conversationId, Long userId);
}
