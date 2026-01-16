package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.dto.ConversationDTO;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI服务适配器，为AI模型提供标准化的接口调用方式
 */
@Service
@RequiredArgsConstructor
public class AIServiceAdapter {

    private final ConversationService conversationService;
    private final UserService userService;
    private final AppInteractionService appInteractionService;

    /**
     * 创建新的对话会话
     *
     * @param userId 用户ID
     * @param appId 应用ID
     * @param title 会话标题
     * @return 会话DTO
     */
    @AIService(
        name = "create_conversation",
        description = "创建一个新的对话会话",
        requiresAuth = true
    )
    public ConversationDTO createConversation(
        @AIParam(name = "userId", description = "用户ID", type = "string", required = true)
        String userId,
        @AIParam(name = "appId", description = "应用ID", type = "string", required = true)
        String appId,
        @AIParam(name = "title", description = "会话标题", type = "string", required = false)
        String title
    ) {
        User user = userService.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Conversation conversation = conversationService.createConversation(appId, user);
        return ConversationDTO.fromEntity(conversation);
    }

    /**
     * 发送消息到指定应用
     *
     * @param userId 用户ID
     * @param appId 应用ID
     * @param conversationId 会话ID
     * @param message 消息内容
     * @return 响应内容
     */
    @AIService(
        name = "send_message_to_app",
        description = "向指定应用发送消息",
        requiresAuth = true
    )
    public String sendMessageToApp(
        @AIParam(name = "userId", description = "用户ID", type = "string", required = true)
        String userId,
        @AIParam(name = "appId", description = "应用ID", type = "string", required = true)
        String appId,
        @AIParam(name = "conversationId", description = "会话ID", type = "string", required = true)
        String conversationId,
        @AIParam(name = "message", description = "消息内容", type = "string", required = true)
        String message
    ) {
        AppChatRequest request = AppChatRequest.builder()
            .query(message)
            .user(userId)
            .conversationId(conversationId)
            .build();

        // 这里我们模拟调用，实际实现可能需要异步处理
        // 在真实实现中，可能需要返回一个任务ID，然后通过其他接口获取结果
        return "Message sent successfully to app " + appId + " in conversation " + conversationId;
    }

    /**
     * 获取用户的所有会话
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @AIService(
        name = "get_user_conversations",
        description = "获取用户的所有会话",
        requiresAuth = true
    )
    public List<ConversationDTO> getUserConversations(
        @AIParam(name = "userId", description = "用户ID", type = "string", required = true)
        String userId
    ) {
        User user = userService.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        List<Conversation> conversations = conversationService.findByUser(user);
        return conversations.stream()
            .map(ConversationDTO::fromEntity)
            .toList();
    }
}