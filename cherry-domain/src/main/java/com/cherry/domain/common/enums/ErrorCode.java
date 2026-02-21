package com.cherry.domain.common.enums;

public enum ErrorCode {

    SUCCESS(0, "OK"),

    UNAUTHORIZED(1001, "未登录或登录已过期"),
    TOKEN_EXPIRED(1002, "登录已过期"),
    INVALID_TOKEN(1003, "非法Token"),

    USER_NOT_FOUND(2001, "用户不存在"),
    PASSWORD_ERROR(2002, "密码错误"),

    NO_PERMISSION(3001, "无权限操作"),

    SYSTEM_ERROR(9999, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() { return code; }
    public String message() { return message; }
}
