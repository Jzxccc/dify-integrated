package com.example.difyintegration.config;

import com.example.difyintegration.security.ReactiveUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        return authentication -> {
            return userDetailsService.findByUsername(authentication.getName())
                .cast(org.springframework.security.core.userdetails.UserDetails.class)
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())
                        && userDetails.isAccountNonExpired()
                        && userDetails.isAccountNonLocked()
                        && userDetails.isCredentialsNonExpired()
                        && userDetails.isEnabled()) {

                        return Mono.just(new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(),
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                        ));
                    } else {
                        return Mono.error(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));
                    }
                });
        };
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) {
                // 使用阻塞方式获取用户信息，因为AuthenticationManager是同步的
                org.springframework.security.core.userdetails.UserDetails userDetails =
                    userDetailsService.findByUsername(authentication.getName())
                        .block();

                if (userDetails != null &&
                    passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())
                    && userDetails.isAccountNonExpired()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isCredentialsNonExpired()
                    && userDetails.isEnabled()) {

                    return new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                    );
                } else {
                    throw new org.springframework.security.authentication.BadCredentialsException("Invalid credentials");
                }
            }
        };
    }
}