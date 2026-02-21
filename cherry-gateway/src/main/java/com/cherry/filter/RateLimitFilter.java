package com.cherry.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * 限流过滤器，用于控制API请求频率。
 * 基于IP地址和请求路径实现细粒度的限流控制，
 * 防止恶意刷接口和保护系统稳定性。
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    private static final int LIMIT = 5;
    private static final int WINDOW = 60; // 秒

    private final RedisScript<Long> script = RedisScript.of(
            """
            local key = KEYS[1]
            local now = tonumber(ARGV[1])
            local window = tonumber(ARGV[2])
            local limit = tonumber(ARGV[3])
            local value = ARGV[4]

            redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

            local count = redis.call('ZCARD', key)

            if count >= limit then
                return count
            end

            redis.call('ZADD', key, now, value)
            redis.call('EXPIRE', key, window)

            return count + 1
            """,
            Long.class
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String ip = Objects.requireNonNull(
                exchange.getRequest().getRemoteAddress()
        ).getAddress().getHostAddress();

        String key = "limit:" + path + ":" + ip;

        long now = System.currentTimeMillis(); // 毫秒
        String uniqueValue = now + "-" + Math.random();

        return redisTemplate.execute(
                script,
                Collections.singletonList(key),
                Arrays.asList(
                        String.valueOf(now),
                        String.valueOf(WINDOW * 1000),
                        String.valueOf(LIMIT),
                        uniqueValue
                )
        ).next().flatMap(count -> {

            System.out.println("当前请求次数 = " + count);

            if (count >= LIMIT) {
                return tooManyRequests(exchange);
            }

            return chain.filter(exchange);
        });
    }

    @Override
    public int getOrder() {
        return -3;
    }

    private Mono<Void> tooManyRequests(ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders()
                .add("Content-Type", "application/json;charset=UTF-8");

        String body = """
        {
          "code": 4001,
          "message": "请求过于频繁，请稍后再试",
          "data": null
        }
        """;

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

