package com.cherry.common;

public interface AuthRedisKey {

    /**
     * 登录失败计数
     */
    String LOGIN_FAIL = "auth:login:fail:";

    /**
     * 登录锁
     */
    String LOGIN_LOCK = "auth:login:lock:";

    /**
     * 用户设备集合
     */
    String USER_DEVICES = "auth:user:devices:";

    /**
     * IP 黑名单
     */
    String IP_BLACK = "auth:ip:black:";
    String BLACK_TOKEN = "black:token:{jti}";
}