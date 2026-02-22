package com.cherry.filter;

import com.cherry.common.constant.AuthRedisKey;
import com.cherry.common.util.FingerprintUtil;
import com.cherry.commons.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Objects;

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

        // ===============================
        // 1️⃣ 白名单
        // ===============================
        if (isWhiteList(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ===============================
        // 2️⃣ 读取 token
        // ===============================
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(authHeader);

        try {
            // ===============================
            // 3️⃣ 解析 JWT
            // ===============================
            Claims claims = JwtUtil.parse(token);

            Long userId = claims.get("userId", Long.class);
            String deviceId = claims.get("deviceId", String.class);
            List<String> roles = (List<String>) claims.get("roles");

            if (userId == null || deviceId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 4️⃣ 黑名单校验（必须在前）
            // ===============================
            String jti = claims.get("jti", String.class);
            String blackKey = AuthRedisKey.BLACK_TOKEN + jti;

            if (Boolean.TRUE.equals(redisTemplate.hasKey(blackKey))) {
                log.warn("token 已进入黑名单 userId={}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 5️⃣ Redis 会话校验
            // ===============================
            String redisKey = "login:" + userId + ":" + deviceId;
            String redisToken = redisTemplate.opsForValue().get(redisKey);

            if (!token.equals(redisToken)) {
                log.warn("token 已失效 userId={} deviceId={}", userId, deviceId);
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 6️⃣ 指纹校验（最后做，最贵）
            // ===============================
            String fp = claims.get("fp", String.class);
            String currentFp = FingerprintUtil.build(request);

            if (fp != null && !Objects.equals(fp, currentFp)) {
                log.warn("token 指纹不匹配 userId={}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 7️⃣ 构建权限
            // ===============================
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (roles != null) {
                for (String role : roles) {
                    authorities.add(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );
                }
            }

            // ===============================
            // 8️⃣ 写入 SecurityContext ⭐⭐⭐⭐⭐
            // ===============================
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

    // ===============================
    // Bearer 处理
    // ===============================
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    // ===============================
    // 白名单
    // ===============================
    private boolean isWhiteList(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/auth/login")
                || path.startsWith("/v3/api-docs");
    }
}