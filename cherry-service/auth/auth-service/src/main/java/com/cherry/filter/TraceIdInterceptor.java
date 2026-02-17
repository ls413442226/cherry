package com.cherry.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class TraceIdInterceptor implements Filter {

    /**
     * 请求过滤器，用于处理请求追踪ID的设置和清理。
     * 从请求头中获取traceId并将其放入MDC上下文中，
     * 便于日志追踪，处理完成后清理MDC上下文。
     *
     * @param servletRequest  Servlet请求对象
     * @param servletResponse Servlet响应对象
     * @param filterChain     过滤器链对象
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;

        // 从请求头中获取追踪ID
        String traceId = req.getHeader("traceId");

        // 将追踪ID放入MDC上下文
        if (traceId == null) {
            MDC.put("traceId", traceId);
        }

        try {
            // 继续执行过滤器链
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 清理MDC上下文
            MDC.clear();
        }
    }

}
