package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.Conversation;
import com.campustrade.entity.Message;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.ConversationMapper;
import com.campustrade.mapper.MessageMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    public Conversation getOrCreate(Long buyerId, Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");

        var q = new LambdaQueryWrapper<Conversation>()
            .eq(Conversation::getBuyerId, buyerId)
            .eq(Conversation::getProductId, productId);

        Long count = conversationMapper.selectCount(q);
        if (count > 0) {
            return conversationMapper.selectOne(q);
        }

        Conversation conv = new Conversation();
        conv.setBuyerId(buyerId);
        conv.setSellerId(p.getSellerId());
        conv.setProductId(productId);
        conversationMapper.insert(conv);
        return conv;
    }

    @Override
    public List<Conversation> listByUser(Long userId) {
        return conversationMapper.selectList(
            new LambdaQueryWrapper<Conversation>()
                .and(w -> w.eq(Conversation::getBuyerId, userId)
                    .or().eq(Conversation::getSellerId, userId))
                .orderByDesc(Conversation::getLastMessageAt)
                .orderByDesc(Conversation::getCreatedAt));
    }

    @Override
    public Message sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) throw new BusinessException("会话不存在");
        if (!senderId.equals(conv.getBuyerId()) && !senderId.equals(conv.getSellerId())) {
            throw new BusinessException("无权访问此会话");
        }

        Message msg = new Message();
        msg.setConversationId(conversationId);
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setIsRead(0);
        messageMapper.insert(msg);

        conv.setLastMessage(content.length() > 100 ? content.substring(0, 100) : content);
        conv.setLastMessageAt(msg.getCreatedAt());
        conversationMapper.updateById(conv);

        return msg;
    }

    @Override
    public void checkParticipant(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) throw new BusinessException("会话不存在");
        if (!userId.equals(conv.getBuyerId()) && !userId.equals(conv.getSellerId())) {
            throw new BusinessException("无权访问此会话");
        }
    }

    @Override
    public List<Message> getMessages(Long conversationId, Long userId) {
        checkParticipant(conversationId, userId);
        return messageMapper.selectList(
            new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationId, conversationId)
                .orderByAsc(Message::getCreatedAt));
    }
}
