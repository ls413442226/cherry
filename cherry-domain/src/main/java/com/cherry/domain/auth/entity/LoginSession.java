package com.cherry.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aaliyah
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSession {

    /** 用户ID */
    private Long userId;

    /** 设备ID */
    private String deviceId;

    /** 浏览器指纹 ⭐⭐⭐⭐⭐ */
    private String fingerprint;
}