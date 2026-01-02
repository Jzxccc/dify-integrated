package com.example.difyintegration.security;

import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> userService.findByUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optUser -> {
                    if (optUser.isPresent()) {
                        com.example.difyintegration.entity.User appUser = optUser.get();
                        return Mono.just(org.springframework.security.core.userdetails.User
                                .withUsername(appUser.getUsername())
                                .password(appUser.getPassword())
                                .authorities("USER") // 添加默认权限
                                .build());
                    } else {
                        return Mono.error(new UsernameNotFoundException("User not found: " + username));
                    }
                });
    }
}