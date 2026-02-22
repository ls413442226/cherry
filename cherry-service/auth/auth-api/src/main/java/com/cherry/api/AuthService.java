package com.cherry.api;


import com.cherry.domain.auth.dto.TokenPair;

public interface AuthService {

    TokenPair login(String username,
                    String password,
                    String deviceId,
                    String ip);

    TokenPair refresh(Long userId,
                      String deviceId,
                      String refreshToken);

    boolean checkLogin(Long userId,
                       String deviceId,
                       String authorization);

    void logout(String token, Long userId, String deviceId);

    boolean isTokenBlacklisted(String accessToken);
}
