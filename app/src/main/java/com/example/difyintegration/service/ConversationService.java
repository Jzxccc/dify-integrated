package com.example.difyintegration.service;

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

    public Conversation createConversation(String appId, User user) {
        Conversation conversation = new Conversation();
        conversation.setAppId(appId);
        conversation.setUser(user);
        conversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        return conversationRepository.save(conversation);
    }

    public Optional<Conversation> findByIdAndUser(String conversationId, User user) {
        return conversationRepository.findByConversationIdAndUser(conversationId, user);
    }

    public List<Conversation> findByUser(User user) {
        return conversationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Conversation updateConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public void endConversation(Conversation conversation) {
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
    public void cleanupExpiredConversationsManually(int days) {
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