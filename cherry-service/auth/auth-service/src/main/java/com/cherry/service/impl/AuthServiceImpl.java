package com.cherry.service.impl;

import com.cherry.api.AuthService;
import com.cherry.common.AuthRedisKey;
import com.cherry.commons.utils.JwtUtil;
import com.cherry.domain.auth.dto.TokenPair;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Aaliyah
 */
@DubboService
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    private static final long ACCESS_EXPIRE_MINUTES = 30;
    private static final long REFRESH_EXPIRE_DAYS = 7;

    @Override
    public TokenPair login(String username,
                           String password,
                           String deviceId) {

        String lockKey = AuthRedisKey.LOGIN_LOCK + username;
        Boolean locked = redisTemplate.hasKey(lockKey);

        if (Boolean.TRUE.equals(locked)) {
            throw new RuntimeException("账号已锁定，请15分钟后再试");
        }

        clearLoginFail(username);



        // ⭐⭐⭐⭐⭐ 1. 交给 Spring Security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Long userId = Long.valueOf(userDetails.getUsername());

        // ⭐ 收集角色
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        // ⭐⭐⭐⭐⭐ 2. 生成 JWT
        String accessToken = JwtUtil.generateAccessToken(userId, deviceId, roles);
        String refreshToken = UUID.randomUUID().toString();

        // ⭐⭐⭐⭐⭐ 3. Redis 会话
        String loginKey = "login:" + userId + ":" + deviceId;
        String refreshKey = "refresh:" + userId + ":" + deviceId;

        redisTemplate.opsForValue().set(
                loginKey, accessToken, ACCESS_EXPIRE_MINUTES, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(
                refreshKey, refreshToken, REFRESH_EXPIRE_DAYS, TimeUnit.DAYS);

        return new TokenPair(
                accessToken,
                refreshToken,
                System.currentTimeMillis() + ACCESS_EXPIRE_MINUTES * 60 * 1000,
                userId,
                username,
                roles
        );
    }

    private void recordLoginFail(String username) {

        String failKey = AuthRedisKey.LOGIN_FAIL + username;
        String lockKey = AuthRedisKey.LOGIN_LOCK + username;

        Long failCount = redisTemplate.opsForValue().increment(failKey);

        // 第一次失败 → 设置过期
        if (failCount != null && failCount == 1) {
            redisTemplate.expire(failKey, 15, TimeUnit.MINUTES);
        }

        // 达到5次 → 锁账号
        if (failCount != null && failCount >= 5) {
            redisTemplate.opsForValue().set(
                    lockKey,
                    "1",
                    15,
                    TimeUnit.MINUTES
            );
        }
    }

    private void clearLoginFail(String username) {
        redisTemplate.delete(AuthRedisKey.LOGIN_FAIL + username);
        redisTemplate.delete(AuthRedisKey.LOGIN_LOCK + username);
    }

    @Override
    public TokenPair refresh(Long userId, String deviceId, String refreshToken) {
        return null;
    }

    @Override
    public boolean checkLogin(Long userId, String deviceId, String authorization) {
        return false;
    }

}

