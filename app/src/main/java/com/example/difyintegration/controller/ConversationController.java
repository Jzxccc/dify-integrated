package com.example.difyintegration.controller;

import com.example.difyintegration.dto.ConversationDTO;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.ConversationService;
import com.example.difyintegration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<ConversationDTO>> createConversation(@RequestBody CreateConversationRequest request,
                                                                   Authentication authentication) {
        return Mono.fromCallable(() -> {
            User user = getCurrentUser(authentication);
            Conversation conversation = conversationService.createConversation(request.getAppId(), user);
            return ResponseEntity.ok(ConversationDTO.fromEntity(conversation));
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{conversationId}")
    public Mono<ResponseEntity<ConversationDTO>> getConversation(@PathVariable String conversationId,
                                                                Authentication authentication) {
        return Mono.fromCallable(() -> {
            User user = getCurrentUser(authentication);
            return conversationService.findByIdAndUser(conversationId, user)
                    .map(conversation -> ResponseEntity.ok(ConversationDTO.fromEntity(conversation)))
                    .orElse(ResponseEntity.notFound().build());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping("/{conversationId}/end")
    public Mono<ResponseEntity<ConversationDTO>> endConversation(@PathVariable String conversationId,
                                                                Authentication authentication) {
        return Mono.fromCallable(() -> {
            User user = getCurrentUser(authentication);
            return conversationService.findByIdAndUser(conversationId, user)
                    .map(conversation -> {
                        conversationService.endConversation(conversation);
                        return ResponseEntity.ok(ConversationDTO.fromEntity(conversation));
                    })
                    .orElse(ResponseEntity.notFound().build());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping
    public Mono<ResponseEntity<List<ConversationDTO>>> getUserConversations(Authentication authentication) {
        return Mono.fromCallable(() -> {
            User user = getCurrentUser(authentication);
            List<Conversation> conversations = conversationService.findByUser(user);
            List<ConversationDTO> dtos = conversations.stream()
                    .map(ConversationDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public static class CreateConversationRequest {
        private String appId;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }
    }
}