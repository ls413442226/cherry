package com.cherry.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * 设备指纹工具（企业级）
 *
 * 作用：
 * 1. 防止 token 被跨设备盗用
 * 2. 绑定设备环境
 *
 * ⚠️ 注意：
 * - 不要只用 IP（移动网络会变）
 * - 指纹不要过于严格（否则误杀）
 */
public class FingerprintUtil {

    private FingerprintUtil() {}

    /**
     * 构建设备指纹
     */
    public static String build(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        String lang = request.getHeader("Accept-Language");
        String deviceId = request.getHeader("X-Device-Id"); // ⭐推荐前端传

        if (!StringUtils.hasText(deviceId)) {
            deviceId = "unknown-device";
        }

        if (!StringUtils.hasText(userAgent)) {
            userAgent = "unknown-ua";
        }

        if (!StringUtils.hasText(lang)) {
            lang = "unknown-lang";
        }

        String raw = deviceId + "|" + userAgent + "|" + lang;

        // ⭐⭐⭐⭐⭐ MD5 足够（不是密码学用途）
        return DigestUtils.md5DigestAsHex(raw.getBytes());
    }
}