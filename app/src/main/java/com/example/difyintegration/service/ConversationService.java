package com.example.difyintegration.service;

import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        conversationRepository.save(conversation);
    }
}