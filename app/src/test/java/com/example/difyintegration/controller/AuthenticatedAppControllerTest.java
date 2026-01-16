package com.example.difyintegration.controller;

import com.example.difyintegration.dto.AppChatRequestDto;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.AppInteractionService;
import com.example.difyintegration.service.ConversationService;
import com.example.difyintegration.service.DifyAppClient;
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
class AuthenticatedAppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppInteractionService appInteractionService;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private DifyAppClient difyAppClient;

    @MockBean
    private UserService userService;

    @Test
    void testSendAppMessageSuccess() throws Exception {
        // Arrange
        String appId = "test-app";
        String username = "testuser";
        String query = "Hello, how are you?";
        
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        AppInteraction mockInteraction = new AppInteraction();
        mockInteraction.setInput(query);
        mockInteraction.setOutput("I'm doing well, thank you!");
        mockInteraction.setTimestamp(LocalDateTime.now());

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(appInteractionService.processAppInteraction(eq(appId), any())).thenReturn(java.util.Optional.of(mockInteraction).map(java.util.Optional::get));

        // Act & Assert
        mockMvc.perform(post("/api/authenticated/app/{appId}/chat", appId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + query + "\"}")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().string("I'm doing well, thank you!"));
    }

    @Test
    void testSendAppMessageWithConversationId() throws Exception {
        // Arrange
        String appId = "test-app";
        String username = "testuser";
        String query = "Hello again!";
        String conversationId = "conv_123";
        
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Conversation mockConversation = new Conversation();
        mockConversation.setConversationId(conversationId);
        mockConversation.setUser(mockUser);

        AppInteraction mockInteraction = new AppInteraction();
        mockInteraction.setInput(query);
        mockInteraction.setOutput("Hello! How can I assist you?");
        mockInteraction.setTimestamp(LocalDateTime.now());

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByIdAndUser(eq(conversationId), eq(mockUser))).thenReturn(Optional.of(mockConversation));
        when(appInteractionService.processAppInteraction(eq(appId), any())).thenReturn(java.util.Optional.of(mockInteraction).map(java.util.Optional::get));

        // Act & Assert
        mockMvc.perform(post("/api/authenticated/app/{appId}/chat", appId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + query + "\", \"conversationId\":\"" + conversationId + "\"}")
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello! How can I assist you?"));
    }

    @Test
    void testSendAppMessageWithInvalidConversationId() throws Exception {
        // Arrange
        String appId = "test-app";
        String username = "testuser";
        String query = "Hello again!";
        String conversationId = "invalid_conv_123";
        
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByIdAndUser(eq(conversationId), eq(mockUser))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/authenticated/app/{appId}/chat", appId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + query + "\", \"conversationId\":\"" + conversationId + "\"}")
                .principal(mockAuth))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAppInteractionHistory() throws Exception {
        // Arrange
        String appId = "test-app";
        String username = "testuser";
        
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        AppInteraction interaction1 = new AppInteraction();
        interaction1.setInput("Hello");
        interaction1.setOutput("Hi there!");
        interaction1.setTimestamp(LocalDateTime.now());

        AppInteraction interaction2 = new AppInteraction();
        interaction2.setInput("How are you?");
        interaction2.setOutput("I'm doing well.");
        interaction2.setTimestamp(LocalDateTime.now());

        List<AppInteraction> interactions = Arrays.asList(interaction1, interaction2);

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(appInteractionService.getInteractionsByAppIdAndUserId(eq(appId), eq("user_123"))).thenReturn(interactions);

        // Act & Assert
        mockMvc.perform(get("/api/authenticated/app/{appId}/history", appId)
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetUserConversations() throws Exception {
        // Arrange
        String appId = "test-app";
        String username = "testuser";
        
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setUserId("user_123");

        Conversation conv1 = new Conversation();
        conv1.setConversationId("conv_1");
        conv1.setAppId(appId);
        conv1.setUser(mockUser);
        conv1.setStatus(Conversation.ConversationStatus.ACTIVE);
        conv1.setCreatedAt(LocalDateTime.now());

        Conversation conv2 = new Conversation();
        conv2.setConversationId("conv_2");
        conv2.setAppId(appId);
        conv2.setUser(mockUser);
        conv2.setStatus(Conversation.ConversationStatus.ENDED);
        conv2.setCreatedAt(LocalDateTime.now());

        List<Conversation> conversations = Arrays.asList(conv1, conv2);

        Authentication mockAuth = new org.springframework.security.authentication.TestingAuthenticationToken(username, null);
        
        when(userService.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(conversationService.findByUser(eq(mockUser))).thenReturn(conversations);

        // Act & Assert
        mockMvc.perform(get("/api/authenticated/app/{appId}/conversations", appId)
                .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].conversationId").value("conv_1"))
                .andExpect(jsonPath("$[1].conversationId").value("conv_2"));
    }
}