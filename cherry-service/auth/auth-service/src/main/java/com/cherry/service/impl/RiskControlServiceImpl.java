package com.cherry.service.impl;

import com.cherry.api.RiskControlService;
import com.cherry.common.constant.AuthRedisKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RiskControlServiceImpl implements RiskControlService {

    private final RedisScript<Long> DEVICE_LUA = RedisScript.of(
            """
            local size = redis.call('SCARD', KEYS[1])
            if size >= tonumber(ARGV[2]) then
                return -1
            end
            redis.call('SADD', KEYS[1], ARGV[1])
            redis.call('EXPIRE', KEYS[1], ARGV[3])
            return size + 1
            """,
            Long.class
    );

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    /** 最大设备数（企业可配） */
    private static final int MAX_DEVICE_COUNT = 5;

    /** IP 短时间阈值 */
    private static final int IP_LIMIT = 20;

    /** 时间窗口（秒） */
    private static final int IP_WINDOW = 60;

    // ===============================
    // 登录前风控
    // ===============================
    @Override
    public void checkLoginRisk(String username, String ip) {

        // ⭐⭐⭐⭐⭐ 1. IP 黑名单
        if (Boolean.TRUE.equals(redisTemplate.hasKey(
                AuthRedisKey.IP_BLACK + ip))) {
            throw new RuntimeException("IP已被封禁");
        }

        // ⭐⭐⭐⭐⭐ 2. IP 频率限制（轻量滑窗）
        String ipKey = "auth:ip:freq:" + ip;

        Long count = redisTemplate.opsForValue().increment(ipKey);

        if (count != null && count == 1) {
            redisTemplate.expire(ipKey, IP_WINDOW, TimeUnit.SECONDS);
        }

        if (count != null && count > IP_LIMIT) {
            log.warn("IP触发风控 ip={}", ip);

            // ⭐ 封禁10分钟
            redisTemplate.opsForValue().set(
                    AuthRedisKey.IP_BLACK + ip,
                    "1",
                    10,
                    TimeUnit.MINUTES
            );

            throw new RuntimeException("操作过于频繁，请稍后再试");
        }
    }

    // ===============================
    // 登录后设备风控
    // ===============================
    @Override
    public void checkDeviceRisk(Long userId,
                                String deviceId,
                                String ip) {

        String deviceKey = AuthRedisKey.USER_DEVICES + userId;

        Long result = redisTemplate.execute(
                DEVICE_LUA,
                Collections.singletonList(deviceKey),
                deviceId,
                String.valueOf(MAX_DEVICE_COUNT),
                String.valueOf(30 * 24 * 3600)
        );

        if (result == null) {
            throw new RuntimeException("设备风控异常");
        }

        if (result == -1) {
            throw new RuntimeException("登录设备数已达上限");
        }

        log.info("设备登记成功 userId={} deviceId={}", userId, deviceId);
    }
}