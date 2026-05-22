package com.campustrade.service;

import com.campustrade.common.BusinessException;
import com.campustrade.entity.Conversation;
import com.campustrade.entity.Message;
import com.campustrade.entity.Product;
import com.campustrade.mapper.ConversationMapper;
import com.campustrade.mapper.MessageMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.impl.ConversationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock private ConversationMapper conversationMapper;
    @Mock private MessageMapper messageMapper;
    @Mock private ProductMapper productMapper;
    @Mock private UserMapper userMapper;
    private ConversationService service;

    @BeforeEach
    void setUp() {
        service = new ConversationServiceImpl(
            conversationMapper, messageMapper, productMapper, userMapper);
    }

    @Test
    void shouldCreateConversation() {
        var p = new Product(); p.setId(1L); p.setSellerId(2L);
        when(productMapper.selectById(1L)).thenReturn(p);
        when(conversationMapper.selectCount(any())).thenReturn(0L);
        when(conversationMapper.insert(any())).thenReturn(1);

        var conv = service.getOrCreate(3L, 1L);

        assertNotNull(conv);
        verify(conversationMapper).insert(any());
    }

    @Test
    void shouldReturnExistingConversation() {
        var p = new Product(); p.setId(1L); p.setSellerId(2L);
        var existing = new Conversation(); existing.setId(10L);
        when(productMapper.selectById(1L)).thenReturn(p);
        when(conversationMapper.selectCount(any())).thenReturn(1L);
        when(conversationMapper.selectOne(any())).thenReturn(existing);

        var conv = service.getOrCreate(3L, 1L);

        assertEquals(10L, conv.getId());
        verify(conversationMapper, never()).insert(any());
    }

    @Test
    void shouldNotCreateConversationWithoutProduct() {
        when(productMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class,
            () -> service.getOrCreate(1L, 999L));
    }

    @Test
    void shouldListUserConversations() {
        when(conversationMapper.selectList(any())).thenReturn(List.of());
        var list = service.listByUser(1L);
        assertTrue(list.isEmpty());
    }

    @Test
    void shouldSendMessage() {
        var conv = new Conversation(); conv.setId(1L);
        conv.setBuyerId(1L); conv.setSellerId(2L);
        when(conversationMapper.selectById(1L)).thenReturn(conv);
        when(messageMapper.insert(any())).thenReturn(1);

        service.sendMessage(1L, 1L, "Hello"); // sender=buyer → OK

        verify(messageMapper).insert(any(Message.class));
        verify(conversationMapper).updateById(conv);
    }

    @Test
    void shouldGetMessages() {
        var conv = new Conversation(); conv.setId(1L);
        conv.setBuyerId(1L); conv.setSellerId(2L);
        when(conversationMapper.selectById(1L)).thenReturn(conv);
        when(messageMapper.selectList(any())).thenReturn(List.of());
        var msgs = service.getMessages(1L, 1L); // user=buyer → OK
        assertTrue(msgs.isEmpty());
    }

    @Test
    void shouldNotSendMessageToUnknownConversation() {
        when(conversationMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class,
            () -> service.sendMessage(999L, 1L, "Hi"));
    }

    @Test
    void shouldRejectNonParticipantSendMessage() {
        var conv = new Conversation(); conv.setId(1L);
        conv.setBuyerId(2L); conv.setSellerId(3L);
        when(conversationMapper.selectById(1L)).thenReturn(conv);

        assertThrows(BusinessException.class,
            () -> service.sendMessage(1L, 999L, "Intruder"));
    }

    @Test
    void shouldRejectNonParticipantGetMessages() {
        var conv = new Conversation(); conv.setId(1L);
        conv.setBuyerId(2L); conv.setSellerId(3L);
        when(conversationMapper.selectById(1L)).thenReturn(conv);

        assertThrows(BusinessException.class,
            () -> service.getMessages(1L, 999L));
    }
}
