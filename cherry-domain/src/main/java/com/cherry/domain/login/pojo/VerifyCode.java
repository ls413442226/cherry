package com.cherry.domain.login.pojo;

import lombok.Data;

@Data
public class VerifyCode {
    // 定义图片的width
    private int width = 90;
    // 定义图片的height
    private int height = 40;
    // 定义图片上显示验证码的个数,改的话要把宽度加长
    private int codeCount = 4;
    private int xx = 15;
    private int fontHeight = 35;
    private int codeY = 30;
    char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '0', '2', '3', '4', '5', '6', '7', '8', '9' };
}
