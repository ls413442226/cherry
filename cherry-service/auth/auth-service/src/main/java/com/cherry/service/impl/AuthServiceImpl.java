package com.cherry.service.impl;

import com.cherry.api.AuthService;
import com.cherry.api.RiskControlService;
import com.cherry.common.constant.AuthRedisKey;
import com.cherry.commons.utils.JwtUtil;
import com.cherry.domain.auth.dto.TokenPair;
import com.cherry.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Objects;


/**
 * @author Aaliyah
 */
@Slf4j
@DubboService
public class AuthServiceImpl implements AuthService {

    @Resource
    private RiskControlService riskControlService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private DefaultRedisScript<Long> refreshCheckScript;

    @Resource
    private UserMapper userMapper;

    /** access token 过期（分钟） */
    private static final long ACCESS_EXPIRE_MINUTES = 30;

    /** refresh token 过期（天） */
    private static final long REFRESH_EXPIRE_DAYS = 7;

    /** 最大失败次数 */
    private static final int MAX_FAIL = 5;

    /** 锁定时间 */
    private static final long LOCK_MINUTES = 15;

    // ==============================
    // 登录（企业级最终版）
    // ==============================
    @Override
    public TokenPair login(String username,
                           String password,
                           String deviceId,
                           String fingerprint,
                           String ip) {

        riskControlService.checkLoginRisk(username, ip);

        // ✅ 0. 基础参数校验（企业必须）
            if (username == null || password == null || deviceId == null) {
            throw new RuntimeException("参数不能为空");
        }

        // ✅ 1. 是否被锁
        String lockKey = AuthRedisKey.LOGIN_LOCK + username;
        Boolean locked = redisTemplate.hasKey(lockKey);

        if (Boolean.TRUE.equals(locked)) {
            throw new RuntimeException("账号已锁定，请15分钟后再试");
        }

        Authentication authentication;

        try {
            // ⭐⭐⭐⭐⭐ 2. Spring Security 认证
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, password);

            authentication = authenticationManager.authenticate(token);

        } catch (Exception ex) {

            // ❗登录失败计数（关键）
            recordLoginFail(username);

            throw new RuntimeException("用户名或密码错误");
        }

        // ✅ 3. 登录成功 → 清除失败记录
        clearLoginFail(username);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Long userId = Long.valueOf(userDetails.getUsername());

        // ✅ 4. 收集角色
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        // ==============================
        // ⭐⭐⭐⭐⭐ 生成 Token
        // ==============================

        String accessToken =
                JwtUtil.generateAccessToken(userId, deviceId, roles,fingerprint);

        String refreshToken = UUID.randomUUID().toString();

        // ⭐⭐⭐⭐⭐ 登录成功后（生成token之后）
        riskControlService.checkDeviceRisk(userId, deviceId, ip);

        // ⭐⭐⭐⭐⭐ 踢掉该设备旧 token
        kickOldDeviceSession(userId, deviceId);

        // ==============================
        // ⭐⭐⭐⭐⭐ Redis 会话（企业级）
        // ==============================

        String loginKey = "login:" + userId + ":" + deviceId;
        String refreshKey = "refresh:" + userId + ":" + deviceId;

        redisTemplate.opsForValue().set(
                loginKey,
                accessToken,
                ACCESS_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        redisTemplate.opsForValue().set(
                refreshKey,
                refreshToken,
                REFRESH_EXPIRE_DAYS,
                TimeUnit.DAYS
        );

        // ==============================
        // ⭐⭐⭐⭐⭐ 返回
        // ==============================

        return new TokenPair(
                accessToken,
                refreshToken,
                System.currentTimeMillis()
                        + ACCESS_EXPIRE_MINUTES * 60 * 1000,
                userId,
                username,
                roles
        );
    }

    // ==============================
    // 登录失败记录（企业级）
    // ==============================
    private void recordLoginFail(String username) {

        String failKey = AuthRedisKey.LOGIN_FAIL + username;
        String lockKey = AuthRedisKey.LOGIN_LOCK + username;

        Long failCount = redisTemplate.opsForValue().increment(failKey);

        // 第一次失败 → 设置过期
        if (failCount != null && failCount == 1) {
            redisTemplate.expire(failKey, LOCK_MINUTES, TimeUnit.MINUTES);
        }

        // 达到阈值 → 锁账号
        if (failCount != null && failCount >= MAX_FAIL) {
            redisTemplate.opsForValue().set(
                    lockKey,
                    "1",
                    LOCK_MINUTES,
                    TimeUnit.MINUTES
            );
        }
    }

    // ==============================
    // 清除失败记录
    // ==============================
    private void clearLoginFail(String username) {
        redisTemplate.delete(AuthRedisKey.LOGIN_FAIL + username);
        redisTemplate.delete(AuthRedisKey.LOGIN_LOCK + username);
    }

    @Override
    public TokenPair refresh(Long userId,
                             String deviceId,
                             String refreshToken,
                             String fingerprint) {

        String refreshKey = "refresh:" + userId + ":" + deviceId;
        log.info("refreshKey={}", refreshKey);
        // 1️⃣ 读取 session
        String cachedRefreshToken = redisTemplate.opsForValue().get(refreshKey);
        log.info("cachedRefreshToken={}", cachedRefreshToken);
        if (cachedRefreshToken == null) {
            throw new RuntimeException("refreshToken 已失效");
        }

        // 2️⃣ 校验 refreshToken 是否匹配
        if (!Objects.equals(cachedRefreshToken, refreshToken)) {
            throw new RuntimeException("refreshToken 校验失败");
        }

        // 3️⃣ Lua 原子删除（防重放）
        Long result = redisTemplate.execute(
                refreshCheckScript,
                Collections.singletonList(refreshKey),
                cachedRefreshToken
        );

        if (result == null || result != 1) {
            throw new RuntimeException("refreshToken 已被使用");
        }

        // 4️⃣ 重新加载角色
        List<String> roles = loadRoles(userId);

        // 5️⃣ 生成新 token
        String newAccessToken =
                JwtUtil.generateAccessToken(userId, deviceId, roles, fingerprint);

        String newRefreshToken = UUID.randomUUID().toString();

        // 6️⃣ 写入新 refresh
        redisTemplate.opsForValue().set(
                refreshKey,
                newRefreshToken,
                REFRESH_EXPIRE_DAYS,
                TimeUnit.DAYS
        );

        // 7️⃣ 更新 access 会话
        redisTemplate.opsForValue().set(
                "login:" + userId + ":" + deviceId,
                newAccessToken,
                ACCESS_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        return new TokenPair(
                newAccessToken,
                newRefreshToken,
                System.currentTimeMillis()
                        + ACCESS_EXPIRE_MINUTES * 60 * 1000,
                userId,
                "username",
                roles
        );
    }


    @Override
    public boolean checkLogin(Long userId,
                              String deviceId,
                              String authorization) {



        authorization = extractToken(authorization);

        if (authorization == null || authorization.isBlank()) {
            return false;
        }

        String loginKey = "login:" + userId + ":" + deviceId;

        String redisToken = redisTemplate.opsForValue().get(loginKey);

        if (redisToken == null) {
            return false;
        }

        // ⭐⭐⭐⭐⭐ 精确匹配
        return authorization.equals(redisToken);
    }

    @Override
    public void logout(String authorization,
                       Long userId,
                       String deviceId) {

        String token = extractToken(authorization);

        // 1️⃣ 拉黑 token
        blacklistToken(token);

        // 2️⃣ 删除会话
        String loginKey = "login:" + userId + ":" + deviceId;
        String refreshKey = "refresh:" + userId + ":" + deviceId;

        redisTemplate.delete(loginKey);
        redisTemplate.delete(refreshKey);
    }

    /**
     * 加载用户角色（refresh / 登录后校验用）
     */
    private List<String> loadRoles(Long userId) {

        List<String> roles = userMapper.selectRoleCodesByUserId(userId);

        if (roles == null || roles.isEmpty()) {
            return List.of();
        }

        return roles;
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }

    /**
     * 拉黑 accessToken（退出登录 / 踢人 / 封号）
     */
    private void blacklistToken(String accessToken) {

        Claims claims = JwtUtil.parse(accessToken);

        String jti = claims.get("jti", String.class);
        Date expireAt = claims.getExpiration();

        long ttl = (expireAt.getTime() - System.currentTimeMillis()) / 1000;

        if (ttl <= 0) {
            return;
        }

        String blackKey = AuthRedisKey.BLACK_TOKEN + jti;

        redisTemplate.opsForValue().set(
                blackKey,
                "1",
                ttl,
                TimeUnit.SECONDS
        );
    }

    @Override
    public boolean isTokenBlacklisted(String accessToken) {

        try {
            Claims claims = JwtUtil.parse(accessToken);
            String jti = claims.get("jti", String.class);

            String blackKey = AuthRedisKey.BLACK_TOKEN + jti;

            return Boolean.TRUE.equals(redisTemplate.hasKey(blackKey));

        } catch (Exception e) {
            return true;
        }
    }

    private void kickOldDeviceSession(Long userId, String deviceId) {

        String loginKey = "login:" + userId + ":" + deviceId;

        String oldToken = redisTemplate.opsForValue().get(loginKey);

        if (oldToken != null) {
            blacklistToken(oldToken);
            log.info("踢掉旧设备会话 userId={} deviceId={}", userId, deviceId);
        }
    }

}
