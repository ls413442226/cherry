package com.cherry.commons.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtUtil {

    // ⭐ access token 30 分钟（毫秒）
    private static final long ACCESS_EXPIRE =
            30 * 60 * 1000L;

    private static final Key key = Keys.hmacShaKeyFor(
            "cherry-auth-super-secret-key-please-change".getBytes() //todo 密钥请更改
    );

    /**
     * 生成 access token（企业版）
     */
        public static String generateAccessToken(Long userId,
                                                 String deviceId,
                                                 List<String> roles,
                                                 String fingerprint) {

            String jti = UUID.randomUUID().toString();

            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("userId", userId)
                    .claim("deviceId", deviceId)
                    .claim("roles", roles)
                    .claim("fp", fingerprint)
                    .claim("jti", jti)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(
                            System.currentTimeMillis() + ACCESS_EXPIRE
                    ))
                    .signWith(key)
                    .compact();
        }

    /**
     * 解析JWT令牌并提取其中的声明信息。
     *
     * @param token 待解析的JWT令牌字符串
     * @return Claims 包含JWT中所有声明信息的对象
     * @throws JwtException 当令牌格式无效、签名验证失败或已过期时抛出异常
     */
    public static Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
