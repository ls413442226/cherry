package com.cherry.controller;

import com.cherry.api.AuthService;
import com.cherry.domain.auth.pojo.TokenPair;
import com.cherry.domain.auth.vo.UserVo;
import com.cherry.domain.vo.LoginResponse;
import com.cherry.domain.vo.RefreshRequest;
import com.cherry.domain.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @DubboReference
    private AuthService authService;

    @Operation(summary = "登陆", description = "登陆")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody UserVo dto) {
        TokenPair pair = authService.login(
                dto.getUsername(),
                dto.getPassword(),
                dto.getDeviceId(),
                dto.getRoles());


        LoginResponse response = new LoginResponse(
                pair.getAccessToken(),
                pair.getRefreshToken(),
                pair.getExpireAt(),
                new LoginResponse.UserInfo(
                        dto.getId(),
                        dto.getUsername(),
                        dto.getRoles()
                )
        );

        return Result.success(response);
    }

    @Operation(summary = "刷新token", description = "刷新token")
    @PostMapping("/refresh")
    public Result<TokenPair> refresh(@RequestBody RefreshRequest request){
        TokenPair pair = authService.refresh(
                request.getUserId(),
                request.getDeviceId(),
                request.getRefreshToken()
        );
        return Result.success(pair);
    }

}
