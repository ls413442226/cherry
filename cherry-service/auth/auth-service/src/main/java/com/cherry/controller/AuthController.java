package com.cherry.controller;

import com.cherry.api.AuthService;
import com.cherry.common.util.FingerprintUtil;
import com.cherry.domain.auth.dto.LoginRequest;
import com.cherry.domain.auth.dto.TokenPair;
import com.cherry.domain.auth.vo.LoginResponse;
import com.cherry.domain.auth.dto.RefreshRequest;
import com.cherry.domain.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author Aaliyah
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @DubboReference
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest dto,
                                       HttpServletRequest httpRequest) {

        // ⭐⭐⭐⭐⭐ 获取真实 IP（企业必须）
        String ip = getClientIp(httpRequest);

        String fingerprint = FingerprintUtil.build(httpRequest);

        TokenPair pair = authService.login(
                dto.getUsername(),
                dto.getPassword(),
                dto.getDeviceId(),
                fingerprint,
                ip
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
            @RequestBody RefreshRequest rReq,
            HttpServletRequest request) {


        TokenPair pair = authService.refresh(
                rReq.getUserId(),
                rReq.getDeviceId(),
                rReq.getRefreshToken(),
                FingerprintUtil.build(request)
        );

        return Result.success(pair);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token,
                               @RequestParam Long userId,
                               @RequestParam String deviceId) {

        authService.logout(token, userId, deviceId);
        return Result.success(null);
    }

    private String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多级代理取第一个
            return ip.split(",")[0];
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }
}

