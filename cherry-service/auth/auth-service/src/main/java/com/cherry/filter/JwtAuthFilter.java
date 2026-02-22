package com.cherry.filter;

import com.cherry.common.AuthRedisKey;
import com.cherry.commons.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aaliyah
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        String path = request.getRequestURI();

        // ✅ 白名单直接放行
        if (isWhiteList(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1️⃣ 读取 Authorization
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2️⃣ 解析 JWT
            Claims claims = JwtUtil.parse(authHeader);

            String jti = claims.get("jti", String.class);
            String blackKey = AuthRedisKey.BLACK_TOKEN + jti;

            Boolean blacklisted = redisTemplate.hasKey(blackKey);
            if (Boolean.TRUE.equals(blacklisted)) {
                log.warn("token 已进入黑名单 userId={}", claims.get("userId"));
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = claims.get("userId", Long.class);
            String deviceId = claims.get("deviceId", String.class);
            List<String> roles = claims.get("roles", List.class);

            if (userId == null || deviceId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3️⃣ Redis 校验（⭐关键企业级）
            String redisKey = "login:" + userId + ":" + deviceId;
            String redisToken = redisTemplate.opsForValue().get(redisKey);

            if (!authHeader.equals(redisToken)) {
                log.warn("token 已失效 userId={} deviceId={}", userId, deviceId);
                filterChain.doFilter(request, response);
                return;
            }

            // 4️⃣ 构建权限（Spring Security 标准格式）
            List<org.springframework.security.core.GrantedAuthority> authorities =
                    new ArrayList<>();

            if (roles != null) {
                for (String role : roles) {
                    authorities.add(
                        new org.springframework.security.core.authority
                            .SimpleGrantedAuthority("ROLE_" + role)
                    );
                }
            }

            // 5️⃣ 写入 Security 上下文 ⭐⭐⭐⭐⭐
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 白名单
     */
    private boolean isWhiteList(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/auth/login")
                || path.startsWith("/v3/api-docs");
    }
}
