package com.cherry.filter;

import com.cherry.api.AuthService;
import com.cherry.commons.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 全局认证过滤器，用于验证JWT令牌并进行权限控制。
 * 实现了Spring Cloud Gateway的GlobalFilter接口和Ordered接口，
 * 用于在请求到达目标服务之前进行统一的身份验证和权限检查。
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITE_LIST = Arrays.asList(
//            "/api/v1/auth/login",
//            "/api/v1/auth/register",
//            "/api/v1/auth/refresh",
            "/**"
    );

    @DubboReference
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (WHITE_LIST.contains(path)) {
            return chain.filter(exchange);
        }

        String authorization =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("Authorization");

        if (authorization == null) {
            return unauthorized(exchange);
        }

        Claims claims;

        try {
            claims = JwtUtil.parse(authorization);
        } catch (Exception e) {
            return unauthorized(exchange);
        }

        Long userId =
                claims.get("userId", Long.class);

        String deviceId =
                claims.get("deviceId", String.class);

        if (userId == null || deviceId == null) {
            return unauthorized(exchange);
        }

        boolean valid =
                authService.checkLogin(
                        userId,
                        deviceId,
                        authorization
                );

        if (!valid) {
            return unauthorized(exchange);
        }

        ServerHttpRequest newRequest =
                exchange.getRequest()
                        .mutate()
                        .header("userId", userId.toString())
                        .build();

        return chain.filter(
                exchange.mutate()
                        .request(newRequest)
                        .build()
        );
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}



