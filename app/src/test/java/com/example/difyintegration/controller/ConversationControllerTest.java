package com.example.difyintegration.controller;

import com.example.difyintegration.dto.ConversationDTO;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.ConversationService;
import com.example.difyintegration.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private UserService userService;

    @Test
    void testCreateConversationSuccess() throws Exception {
        // Arrange
        String appId = "test-app";
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Conversation mockConversation = new Conversation();
        mockConversation.setConversationId("conv_123");
        mockConversation.setAppId(appId);
        mockConversation.setUser(mockUser);
        mockConversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        mockConversation.setCreatedAt(LocalDateTime.now());

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.createConversation(eq(appId), eq(mockUser))).thenReturn(mockConversation);

        // Act & Assert
        mockMvc.perform(post("/api/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appId\":\"" + appId + "\"}")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.conversationId").value("conv_123"))
                .andExpect(jsonPath("$.appId").value(appId))
                .andExpect(jsonPath("$.userId").value("user_123"));
    }

    @Test
    void testGetConversationSuccess() throws Exception {
        // Arrange
        String conversationId = "conv_123";
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Conversation mockConversation = new Conversation();
        mockConversation.setConversationId(conversationId);
        mockConversation.setAppId("test-app");
        mockConversation.setUser(mockUser);
        mockConversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        mockConversation.setCreatedAt(LocalDateTime.now());

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByIdAndUser(eq(conversationId), eq(mockUser))).thenReturn(Optional.of(mockConversation));

        // Act & Assert
        mockMvc.perform(get("/api/conversations/" + conversationId)
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.conversationId").value(conversationId));
    }

    @Test
    void testGetConversationNotFound() throws Exception {
        // Arrange
        String conversationId = "conv_123";
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByIdAndUser(eq(conversationId), eq(mockUser))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/conversations/" + conversationId)
                .principal(mockAuth))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEndConversationSuccess() throws Exception {
        // Arrange
        String conversationId = "conv_123";
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Conversation mockConversation = new Conversation();
        mockConversation.setConversationId(conversationId);
        mockConversation.setAppId("test-app");
        mockConversation.setUser(mockUser);
        mockConversation.setStatus(Conversation.ConversationStatus.ACTIVE);
        mockConversation.setCreatedAt(LocalDateTime.now());

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByIdAndUser(eq(conversationId), eq(mockUser))).thenReturn(Optional.of(mockConversation));

        // Act & Assert
        mockMvc.perform(put("/api/conversations/" + conversationId + "/end")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.conversationId").value(conversationId))
                .andExpect(jsonPath("$.status").value("ENDED"));
    }

    @Test
    void testGetUserConversations() throws Exception {
        // Arrange
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Conversation conv1 = new Conversation();
        conv1.setConversationId("conv_1");
        conv1.setAppId("test-app");
        conv1.setUser(mockUser);
        conv1.setStatus(Conversation.ConversationStatus.ACTIVE);
        conv1.setCreatedAt(LocalDateTime.now());

        Conversation conv2 = new Conversation();
        conv2.setConversationId("conv_2");
        conv2.setAppId("test-app");
        conv2.setUser(mockUser);
        conv2.setStatus(Conversation.ConversationStatus.ENDED);
        conv2.setCreatedAt(LocalDateTime.now());

        List<Conversation> conversations = Arrays.asList(conv1, conv2);

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByUser(eq(mockUser))).thenReturn(conversations);

        // Act & Assert
        mockMvc.perform(get("/api/conversations")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].conversationId").value("conv_1"))
                .andExpect(jsonPath("$[1].conversationId").value("conv_2"));
    }
}