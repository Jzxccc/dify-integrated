package com.example.difyintegration.security;

import com.example.difyintegration.service.UserService;
import com.example.difyintegration.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestTokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT token");
            } catch (Exception e) {
                log.error("Error occurred while extracting username from JWT token", e);
            }
        } else {
            log.debug("Authorization header does not start with Bearer");
        }

        if (username != null && exchange.getAttribute("authentication") == null) {
            final String finalUsername = username;
            final String finalJwtToken = jwtToken;

            return Mono.fromCallable(() -> userService.findByUsername(finalUsername))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(optionalUser -> {
                        if (optionalUser.isPresent() && jwtUtil.validateToken(finalJwtToken, finalUsername)) {
                            com.example.difyintegration.entity.User user = optionalUser.get();
                            UserDetails userDetails = org.springframework.security.core.userdetails.User
                                    .withUsername(user.getUsername())
                                    .password(user.getPassword())
                                    .build();

                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());

                            SecurityContext context = new SecurityContextImpl();
                            context.setAuthentication(authToken);

                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                        } else {
                            return chain.filter(exchange);
                        }
                    })
                    .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
        } else {
            return chain.filter(exchange);
        }
    }
}