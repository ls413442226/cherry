package com.cherry.controller;

import com.cherry.api.AuthService;
import com.cherry.domain.auth.dto.LoginRequest;
import com.cherry.domain.auth.dto.TokenPair;
import com.cherry.domain.auth.vo.LoginResponse;
import com.cherry.domain.auth.dto.RefreshRequest;
import com.cherry.domain.common.result.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aaliyah
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @DubboReference
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest dto) {

        TokenPair pair = authService.login(
                dto.getUsername(),
                dto.getPassword(),
                dto.getDeviceId()
        );

        LoginResponse response =
                new LoginResponse(
                        pair.getAccessToken(),
                        pair.getRefreshToken(),
                        pair.getExpireAt(),
                        new LoginResponse.UserInfo(
                                pair.getUserId(),
                                pair.getUsername(),
                                pair.getRoles()
                        )
                );

        return Result.success(response);
    }

    @PostMapping("/refresh")
    public Result<TokenPair> refresh(
            @RequestBody RefreshRequest request) {

        TokenPair pair = authService.refresh(
                request.getUserId(),
                request.getDeviceId(),
                request.getRefreshToken()
        );

        return Result.success(pair);
    }
}

