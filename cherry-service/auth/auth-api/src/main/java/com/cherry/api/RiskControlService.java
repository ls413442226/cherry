package com.cherry.api;

public interface RiskControlService {

    /**
     * 登录风控检查（登录前）
     */
    void checkLoginRisk(String username, String ip);

    /**
     * 登录后设备风控
     */
    void checkDeviceRisk(Long userId, String deviceId, String ip);
}