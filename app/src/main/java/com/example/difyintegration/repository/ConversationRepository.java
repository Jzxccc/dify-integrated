package com.example.difyintegration.repository;

import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserOrderByCreatedAtDesc(User user);
    Optional<Conversation> findByConversationIdAndUser(String conversationId, User user);
    List<Conversation> findByUserUserId(String userUserId);
}