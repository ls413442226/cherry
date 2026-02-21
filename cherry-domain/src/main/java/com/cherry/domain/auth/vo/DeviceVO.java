package com.cherry.domain.auth.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceVO {

    private String deviceId;

    private String deviceType;

    private String deviceName;

    private String ipAddress;

    private LocalDateTime lastLoginTime;

    private Boolean online;
}
