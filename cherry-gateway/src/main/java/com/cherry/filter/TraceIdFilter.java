package com.cherry.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilter implements GatewayFilter, Ordered{

/**
 * 网关过滤器，用于生成和传递请求追踪ID。
 * 在每个请求中生成唯一的traceId并添加到请求头中，
 * 便于后续服务间的请求追踪和日志关联。
 *
 * @param exchange ServerWebExchange对象，包含HTTP请求和响应信息
 * @param chain GatewayFilterChain对象，用于继续执行过滤器链
 * @return Mono<Void> 异步响应结果
 */
private static final String TRACE_ID = "traceId";
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    // 生成唯一的追踪ID
    String traceId = UUID.randomUUID().toString().replace("-", "");

    // 将traceId添加到请求头中
    ServerHttpRequest newRequest = exchange.getRequest()
            .mutate()
            .header(TRACE_ID, traceId)
            .build();

    // 继续执行过滤器链
    return chain.filter(exchange.mutate().request(newRequest).build());
}


    @Override
    public int getOrder() {
        return -2;
    }
}
