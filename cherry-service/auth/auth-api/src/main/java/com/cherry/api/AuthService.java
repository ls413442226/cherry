package com.cherry.api;


import com.cherry.domain.auth.pojo.TokenPair;

import java.util.List;

public interface AuthService {

    TokenPair login(String username, String password, String deviceId, List<String> roles);

    boolean checkLogin(Long userId, String deviceId);

    TokenPair refresh(Long userId, String deviceId, String refreshToken);
}
