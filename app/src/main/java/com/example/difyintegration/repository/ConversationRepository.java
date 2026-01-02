package com.example.difyintegration.repository;

import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserOrderByCreatedAtDesc(User user);
    Optional<Conversation> findByConversationIdAndUser(String conversationId, User user);
    List<Conversation> findByUserUserId(String userUserId);

    /**
     * 查找指定状态且在指定时间之前更新的会话
     */
    @Query("SELECT c FROM Conversation c WHERE c.status = :status AND c.updatedAt < :updatedAt")
    List<Conversation> findByStatusAndUpdatedAtBefore(@Param("status") Conversation.ConversationStatus status,
                                                     @Param("updatedAt") LocalDateTime updatedAt);
}