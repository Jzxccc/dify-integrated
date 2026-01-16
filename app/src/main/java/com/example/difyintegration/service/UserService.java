package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @AIService(
        name = "find_user_by_username",
        description = "根据用户名查找用户",
        requiresAuth = false
    )
    public Optional<User> findByUsername(
        @AIParam(name = "username", description = "用户名", type = "string", required = true)
        String username) {
        return userRepository.findByUsername(username);
    }

    @AIService(
        name = "find_user_by_user_id",
        description = "根据用户ID查找用户",
        requiresAuth = false
    )
    public Optional<User> findByUserId(
        @AIParam(name = "userId", description = "用户ID", type = "string", required = true)
        String userId) {
        return userRepository.findByUserId(userId);
    }

    @AIService(
        name = "find_user_by_email",
        description = "根据邮箱查找用户",
        requiresAuth = false
    )
    public Optional<User> findByEmail(
        @AIParam(name = "email", description = "邮箱", type = "string", required = true)
        String email) {
        return userRepository.findByEmail(email);
    }

    @AIService(
        name = "create_user",
        description = "创建新用户",
        requiresAuth = false
    )
    public User createUser(
        @AIParam(name = "username", description = "用户名", type = "string", required = true)
        String username,
        @AIParam(name = "email", description = "邮箱", type = "string", required = true)
        String email,
        @AIParam(name = "password", description = "密码", type = "string", required = true)
        String password) {
        // 检查用户名或邮箱是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    @AIService(
        name = "save_user",
        description = "保存用户信息",
        requiresAuth = true
    )
    public User save(
        @AIParam(name = "user", description = "用户对象", type = "object", required = true)
        User user) {
        return userRepository.save(user);
    }
}