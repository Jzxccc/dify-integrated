package com.example.difyintegration.controller;

import com.example.difyintegration.dto.auth.AuthRequest;
import com.example.difyintegration.dto.auth.AuthResponse;
import com.example.difyintegration.dto.auth.RegisterRequest;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.UserService;
import com.example.difyintegration.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        return Mono.fromCallable(() -> {
            // 验证用户凭据
            var userOpt = userService.findByUsername(authRequest.getUsername());
            if (userOpt.isPresent() && passwordEncoder.matches(authRequest.getPassword(), userOpt.get().getPassword())) {
                String token = jwtUtil.generateToken(authRequest.getUsername());
                return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
            } else {
                return ResponseEntity.status(401).body(null);
            }
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody RegisterRequest registerRequest) {
        return Mono.fromCallable(() -> {
            // 检查用户名和邮箱是否已存在
            if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(new AuthResponse(null, "Username already exists"));
            }

            if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(new AuthResponse(null, "Email already exists"));
            }

            User user = userService.createUser(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );

            // 登录新用户并返回token
            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, "Registration successful"));
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorReturn(ResponseEntity.badRequest().body(null));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<AuthResponse>> logout() {
        // 在实际应用中，可能需要将JWT加入黑名单
        return Mono.just(ResponseEntity.ok(new AuthResponse(null, "Logout successful")));
    }
}