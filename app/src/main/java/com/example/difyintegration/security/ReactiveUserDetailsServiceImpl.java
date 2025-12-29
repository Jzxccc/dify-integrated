package com.example.difyintegration.security;

import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> userService.findByUsername(username))
                .subscribeOn(org.springframework.core.scheduler.Schedulers.boundedElastic())
                .flatMap(optUser -> {
                    if (optUser.isPresent()) {
                        User user = optUser.get();
                        return Mono.just(org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword())
                                .build());
                    } else {
                        return Mono.empty();
                    }
                });
    }
}