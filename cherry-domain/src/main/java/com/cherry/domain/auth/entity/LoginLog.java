package com.cherry.domain.auth.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体
 * 对应表：sys_login_log
 * @author Aaliyah
 */
@Data
public class LoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 登录用户名 */
    private String username;

    /** 设备ID */
    private String deviceId;

    /** 设备类型 */
    private String deviceType;

    /** 登录IP地址 */
    private String ipAddress;

    /** 登录状态：1成功 0失败 */
    private Integer loginStatus;

    /** 失败原因 */
    private String failReason;

    /** 登录时间 */
    private LocalDateTime createdAt;
}

