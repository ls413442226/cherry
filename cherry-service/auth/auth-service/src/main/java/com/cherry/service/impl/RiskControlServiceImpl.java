package com.cherry.service.impl;

import com.cherry.api.RiskControlService;
import com.cherry.common.AuthRedisKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RiskControlServiceImpl implements RiskControlService {

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

        // ⭐⭐⭐⭐⭐ 使用 Set 存设备（企业标准）
        Long size = redisTemplate.opsForSet().size(deviceKey);

        if (size != null && size >= MAX_DEVICE_COUNT) {

            // ⭐ 企业策略：拒绝新设备（更安全）
            throw new RuntimeException("登录设备数已达上限");

            // —— 如果你想挤下线，我可以给你版本2
        }

        // ⭐ 记录设备
        redisTemplate.opsForSet().add(deviceKey, deviceId);

        // ⭐ 设置长期过期（可选）
        redisTemplate.expire(deviceKey, 30, TimeUnit.DAYS);

        log.info("设备登记 userId={} deviceId={}", userId, deviceId);
    }
}