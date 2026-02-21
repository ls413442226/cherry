package com.cherry.domain.auth.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户设备实体
 * 对应表：sys_user_device
 * @author Aaliyah
 */
@Data
public class UserDevice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 设备主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 设备唯一标识 */
    private String deviceId;

    /** 设备类型（WEB ANDROID IOS WINDOWS） */
    private String deviceType;

    /** 设备名称 */
    private String deviceName;

    /** 登录IP地址 */
    private String ipAddress;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 在线状态：1在线 0下线 */
    private Integer status;

    /** 逻辑删除标记 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

