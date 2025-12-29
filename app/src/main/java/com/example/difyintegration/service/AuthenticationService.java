package com.example.difyintegration.service;

import com.example.difyintegration.dto.auth.AuthRequest;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveUserDetailsService userDetailsService;

    public Mono<String> login(AuthRequest authRequest) {
        UsernamePasswordAuthenticationToken token = 
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), 
                authRequest.getPassword()
            );
        
        return Mono.fromCallable(() -> {
            Authentication authentication = authenticationManager.authenticate(token);
            return jwtUtil.generateToken(authentication.getName());
        });
    }
}