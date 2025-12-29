package com.example.difyintegration.config;

import com.example.difyintegration.security.ReactiveUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.web.server.WebFilterExchange;
import reactor.core.publisher.Mono;

@Configuration
public class AuthenticationConfig {

    private final PasswordEncoder passwordEncoder;
    private final ReactiveUserDetailsServiceImpl userDetailsService;

    public AuthenticationConfig(PasswordEncoder passwordEncoder, ReactiveUserDetailsServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return authentication -> {
            return Mono.fromCallable(() -> provider.authenticate(authentication));
        };
    }
}