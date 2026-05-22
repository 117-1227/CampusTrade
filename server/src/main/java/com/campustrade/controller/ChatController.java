package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Conversation;
import com.campustrade.entity.Message;
import com.campustrade.security.JwtTokenProvider;
import com.campustrade.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ChatController {

    private final ConversationService conversationService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Result<Conversation> create(@RequestBody Map<String, Long> body,
                                        @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(conversationService.getOrCreate(userId, body.get("productId")));
    }

    @GetMapping
    public Result<List<Conversation>> list(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(conversationService.listByUser(userId));
    }

    @GetMapping("/{id}/messages")
    public Result<List<Message>> messages(@PathVariable Long id,
                                           @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(conversationService.getMessages(id, userId));
    }

    @PostMapping("/{id}/messages")
    public Result<Message> send(@PathVariable Long id,
                                 @RequestBody Map<String, String> body,
                                 @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(conversationService.sendMessage(id, userId, body.get("content")));
    }
}
