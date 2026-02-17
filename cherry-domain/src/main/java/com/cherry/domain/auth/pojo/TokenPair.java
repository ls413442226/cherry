package com.cherry.domain.auth.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class TokenPair  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String accessToken;
    private String refreshToken;
    private Long expireAt;

    private Long userId;
    private String username;

    private List<String> roles;
}