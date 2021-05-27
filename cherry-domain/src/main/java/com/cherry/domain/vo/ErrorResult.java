package com.cherry.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResult {

    private String errCode;
    private String errMessage;

    public static ErrorResult error() {
        return ErrorResult.builder().errCode("9").errMessage("系统异常稍后再试").build();
    }

    public static ErrorResult fail() {
        return ErrorResult.builder().errCode("0").errMessage("发送验证码失败").build();
    }

    public static ErrorResult duplicate() {
        return ErrorResult.builder().errCode("01").errMessage("上一次发送的验证码还未失效").build();
    }

    public static ErrorResult loginError() {
        return ErrorResult.builder().errCode("02").errMessage("验证失败或验证码过期").build();
    }

    public static ErrorResult faceError() {
        return ErrorResult.builder().errCode("03").errMessage("图片非人像，请重新上传!").build();
    }
    public static ErrorResult phoneError() {
        return ErrorResult.builder().errCode("03").errMessage("手机号或用户名为空,请重新填写!").build();
    }
    public static ErrorResult success(){
        return ErrorResult.builder().errCode("00").errMessage("注册成功").build();
    }
    public static ErrorResult codeRrror(){
        return ErrorResult.builder().errCode("01").errMessage("验证码错误").build();
    }
}
