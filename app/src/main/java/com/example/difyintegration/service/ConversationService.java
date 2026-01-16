package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;

    @AIService(
        name = "create_conversation",
        description = "创建一个新的对话会话",
        requiresAuth = true
    )
    public Conversation createConversation(
        @AIParam(name = "appId", description = "应用ID", type = "string", required = true)
        String appId,
        @AIParam(name = "user", description = "用户对象", type = "object", required = true)
        User user) {
        Conversation conversation = new Conversation();
        conversation.setAppId(appId);
        conversation.setUser(user);
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        return conversationRepository.save(conversation);
    }

    @AIService(
        name = "get_conversation_by_id_and_user",
        description = "根据会话ID和用户获取会话信息",
        requiresAuth = true
    )
    public Optional<Conversation> findByIdAndUser(
        @AIParam(name = "conversationId", description = "会话ID", type = "string", required = true)
        String conversationId,
        @AIParam(name = "user", description = "用户对象", type = "object", required = true)
        User user) {
        return conversationRepository.findByConversationIdAndUser(conversationId, user);
    }

    @AIService(
        name = "get_conversations_by_user",
        description = "根据用户获取所有会话",
        requiresAuth = true
    )
    public List<Conversation> findByUser(
        @AIParam(name = "user", description = "用户对象", type = "object", required = true)
        User user) {
        return conversationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @AIService(
        name = "update_conversation",
        description = "更新会话信息",
        requiresAuth = true
    )
    public Conversation updateConversation(
        @AIParam(name = "conversation", description = "会话对象", type = "object", required = true)
        Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    @AIService(
        name = "end_conversation",
        description = "结束一个会话",
        requiresAuth = true
    )
    public void endConversation(
        @AIParam(name = "conversation", description = "会话对象", type = "object", required = true)
        Conversation conversation) {
        conversation.setStatus(Conversation.ConversationStatus.ENDED);
        conversation.setEndedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    /**
     * 自动清理超过指定时间的非活跃会话
     * 每小时运行一次
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void cleanupExpiredConversations() {
        // 假设会话在30天内没有活动则自动结束
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(30);

        List<Conversation> expiredConversations = conversationRepository
            .findByStatusAndUpdatedAtBefore(Conversation.ConversationStatus.ACTIVE, expirationTime);

        for (Conversation conversation : expiredConversations) {
            conversation.setStatus(Conversation.ConversationStatus.ENDED);
            conversation.setEndedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
            log.info("自动清理过期会话: {}", conversation.getConversationId());
        }
    }

    /**
     * 手动清理过期会话的方法
     */
    @AIService(
        name = "cleanup_expired_conversations_manually",
        description = "手动清理过期会话",
        requiresAuth = true
    )
    public void cleanupExpiredConversationsManually(
        @AIParam(name = "days", description = "天数", type = "integer", required = true)
        int days) {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(days);

        List<Conversation> expiredConversations = conversationRepository
            .findByStatusAndUpdatedAtBefore(Conversation.ConversationStatus.ACTIVE, expirationTime);

        int count = 0;
        for (Conversation conversation : expiredConversations) {
            conversation.setStatus(Conversation.ConversationStatus.ENDED);
            conversation.setEndedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
            log.info("手动清理过期会话: {}", conversation.getConversationId());
            count++;
        }

        log.info("手动清理完成，共清理 {} 个会话", count);
    }
}