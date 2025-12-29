package com.example.difyintegration.service;

import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.repository.ConversationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void testCreateConversation() {
        // Arrange
        String appId = "test-app";
        User user = new User();
        user.setId(1L);
        
        Conversation savedConversation = new Conversation();
        savedConversation.setAppId(appId);
        savedConversation.setUser(user);
        savedConversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        
        when(conversationRepository.save(any(Conversation.class))).thenReturn(savedConversation);

        // Act
        Conversation result = conversationService.createConversation(appId, user);

        // Assert
        assertEquals(appId, result.getAppId());
        assertEquals(user, result.getUser());
        assertEquals(Conversation.ConversationStatus.ACTIVE, result.getStatus());
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void testFindByIdAndUser() {
        // Arrange
        String conversationId = "conv-123";
        User user = new User();
        user.setId(1L);
        
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setUser(user);
        
        when(conversationRepository.findByConversationIdAndUser(conversationId, user))
            .thenReturn(Optional.of(conversation));

        // Act
        Optional<Conversation> result = conversationService.findByIdAndUser(conversationId, user);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(conversationId, result.get().getConversationId());
        verify(conversationRepository).findByConversationIdAndUser(conversationId, user);
    }

    @Test
    void testFindByUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        
        Conversation conv1 = new Conversation();
        conv1.setConversationId("conv-1");
        conv1.setUser(user);
        
        Conversation conv2 = new Conversation();
        conv2.setConversationId("conv-2");
        conv2.setUser(user);
        
        List<Conversation> conversations = Arrays.asList(conv1, conv2);
        
        when(conversationRepository.findByUserOrderByCreatedAtDesc(user))
            .thenReturn(conversations);

        // Act
        List<Conversation> result = conversationService.findByUser(user);

        // Assert
        assertEquals(2, result.size());
        verify(conversationRepository).findByUserOrderByCreatedAtDesc(user);
    }
}