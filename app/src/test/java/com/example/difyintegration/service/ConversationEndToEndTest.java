package com.example.difyintegration.service;

import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.repository.AppInteractionRepository;
import com.example.difyintegration.repository.ConversationRepository;
import com.example.difyintegration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class ConversationEndToEndTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private AppInteractionService appInteractionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private AppInteractionRepository appInteractionRepository;

    private User testUser;
    private String testAppId = "test-app";

    @BeforeEach
    void setUp() {
        // 清理测试数据
        appInteractionRepository.deleteAll();
        conversationRepository.deleteAll();
        userRepository.deleteAll();

        // 创建测试用户
        testUser = userService.createUser("testuser", "test@example.com", "password");
    }

    @Test
    void testMultiConversationEndToEnd() throws Exception {
        // 1. 创建第一个会话
        Conversation conversation1 = conversationService.createConversation(testAppId, testUser);
        assertNotNull(conversation1.getConversationId());
        assertEquals(testAppId, conversation1.getAppId());
        assertEquals(testUser.getId(), conversation1.getUser().getId());
        assertEquals(Conversation.ConversationStatus.ACTIVE, conversation1.getStatus());

        // 2. 创建第二个会话
        Conversation conversation2 = conversationService.createConversation(testAppId, testUser);
        assertNotNull(conversation2.getConversationId());
        assertNotEquals(conversation1.getConversationId(), conversation2.getConversationId());
        assertEquals(testUser.getId(), conversation2.getUser().getId());

        // 3. 在第一个会话中添加交互
        AppChatRequest request1 = AppChatRequest.builder()
                .query("Hello from session 1")
                .user(testUser.getUserId())
                .conversationId(conversation1.getConversationId())
                .build();

        AppInteraction interaction1 = new AppInteraction();
        interaction1.setAppId(testAppId);
        interaction1.setUserId(testUser.getUserId());
        interaction1.setConversationId(conversation1.getConversationId());
        interaction1.setInput(request1.getQuery());
        interaction1.setOutput("Response to session 1");
        
        // 模拟保存交互
        AppInteraction savedInteraction1 = appInteractionRepository.save(interaction1);
        assertNotNull(savedInteraction1.getId());

        // 4. 在第二个会话中添加交互
        AppChatRequest request2 = AppChatRequest.builder()
                .query("Hello from session 2")
                .user(testUser.getUserId())
                .conversationId(conversation2.getConversationId())
                .build();

        AppInteraction interaction2 = new AppInteraction();
        interaction2.setAppId(testAppId);
        interaction2.setUserId(testUser.getUserId());
        interaction2.setConversationId(conversation2.getConversationId());
        interaction2.setInput(request2.getQuery());
        interaction2.setOutput("Response to session 2");
        
        // 模拟保存交互
        AppInteraction savedInteraction2 = appInteractionRepository.save(interaction2);
        assertNotNull(savedInteraction2.getId());

        // 5. 验证两个会话的交互是分开的
        List<AppInteraction> interactionsForSession1 = appInteractionRepository.findByConversationId(conversation1.getConversationId());
        List<AppInteraction> interactionsForSession2 = appInteractionRepository.findByConversationId(conversation2.getConversationId());

        assertEquals(1, interactionsForSession1.size());
        assertEquals(1, interactionsForSession2.size());
        assertEquals("Hello from session 1", interactionsForSession1.get(0).getInput());
        assertEquals("Hello from session 2", interactionsForSession2.get(0).getInput());

        // 6. 验证用户可以获取自己的所有会话
        List<Conversation> userConversations = conversationService.findByUser(testUser);
        assertEquals(2, userConversations.size());

        // 7. 结束第一个会话
        conversationService.endConversation(conversation1);
        Optional<Conversation> updatedConversation1 = conversationRepository.findById(conversation1.getId());
        assertTrue(updatedConversation1.isPresent());
        assertEquals(Conversation.ConversationStatus.ENDED, updatedConversation1.get().getStatus());

        // 8. 验证第二个会话仍然活跃
        Optional<Conversation> updatedConversation2 = conversationRepository.findById(conversation2.getId());
        assertTrue(updatedConversation2.isPresent());
        assertEquals(Conversation.ConversationStatus.ACTIVE, updatedConversation2.get().getStatus());
    }

    @Test
    void testConversationIsolationBetweenUsers() {
        // 创建第二个测试用户
        User testUser2 = userService.createUser("testuser2", "test2@example.com", "password");

        // 第一个用户创建会话
        Conversation user1Conversation = conversationService.createConversation(testAppId, testUser);
        assertNotNull(user1Conversation.getConversationId());

        // 第二个用户应该无法访问第一个用户的会话
        Optional<Conversation> accessAttempt = conversationService.findByIdAndUser(
                user1Conversation.getConversationId(), testUser2);
        assertTrue(accessAttempt.isEmpty());

        // 第一个用户应该能够访问自己的会话
        Optional<Conversation> ownAccess = conversationService.findByIdAndUser(
                user1Conversation.getConversationId(), testUser);
        assertTrue(ownAccess.isPresent());
        assertEquals(user1Conversation.getConversationId(), ownAccess.get().getConversationId());
    }
}