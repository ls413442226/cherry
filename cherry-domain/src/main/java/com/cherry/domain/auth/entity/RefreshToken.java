package com.cherry.domain.auth.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 刷新令牌实体
 * 对应表：sys_refresh_token
 * @author Aaliyah
 */
@Data
public class RefreshToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 设备ID */
    private String deviceId;

    /** 刷新令牌 */
    private String refreshToken;

    /** 过期时间 */
    private LocalDateTime expireAt;

    /** 是否已作废：0否 1是 */
    private Integer isRevoked;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

