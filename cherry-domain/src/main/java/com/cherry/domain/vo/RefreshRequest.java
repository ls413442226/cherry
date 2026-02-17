package com.cherry.domain.vo;

import lombok.Data;

@Data
public class RefreshRequest {

    private Long userId;
    private String deviceId;
    private String refreshToken;

}
