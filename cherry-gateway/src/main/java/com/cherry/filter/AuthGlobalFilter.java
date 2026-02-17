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

    /**
     * 白名单路径列表，这些路径不需要进行身份验证。
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh"
    );

    /**
     * Dubbo远程调用认证服务接口。
     */
    @DubboReference
    private AuthService authService;

    /**
     * 过滤器核心方法，处理每个经过网关的请求。
     *
     * @param exchange ServerWebExchange对象，包含HTTP请求和响应信息
     * @param chain GatewayFilterChain对象，用于继续执行过滤器链
     * @return Mono<Void> 异步响应结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // 1️⃣ 白名单放行
        if (WHITE_LIST.contains(path)) {
            return chain.filter(exchange);
        }

        // 2️⃣ 读取 Authorization
        String authorization = exchange.getRequest()
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

        Long userId = claims.get("userId", Long.class);
        String deviceId = claims.get("deviceId", String.class);

        if (userId == null || deviceId == null) {
            return unauthorized(exchange);
        }

        boolean valid = authService.checkLogin(userId, deviceId);

        if (!valid) {
            return unauthorized(exchange);
        }

        // 3️⃣ 权限控制
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) roles = List.of();

        if (path.startsWith("/api/v1/blog/admin")
                && !roles.contains("ADMIN")) {
            return forbidden(exchange);
        }

        // 4️⃣ 传递 userId
        ServerHttpRequest newRequest = exchange.getRequest()
                .mutate()
                .header("userId", userId.toString())
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 返回未授权错误响应。
     *
     * @param exchange ServerWebExchange对象
     * @return Mono<Void> 异步响应结果
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * 返回禁止访问错误响应。
     *
     * @param exchange ServerWebExchange对象
     * @return Mono<Void> 异步响应结果
     */
    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    /**
     * 设置过滤器执行顺序，数值越小优先级越高。
     *
     * @return int 过滤器执行顺序
     */
    @Override
    public int getOrder() {
        return -1;
    }
}


