package com.cherry.commons.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtUtil {

    private static final String SECRET = "Cherry-platform-secret-keydsxaswq"; //平台密钥后续自定义
    private static final long ACCESS_EXPIRE = 30 * 60 * 1000; //30分钟

    private static final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static String generateAccessToken(Long userId, String deviceId, List<String> roles){
        Date now = new Date();
        Date expire = new Date(now.getTime() + ACCESS_EXPIRE);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("deviceId",deviceId)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌并提取其中的声明信息。
     *
     * @param token 待解析的JWT令牌字符串
     * @return Claims 包含JWT中所有声明信息的对象
     * @throws JwtException 当令牌格式无效、签名验证失败或已过期时抛出异常
     */
    public static Claims parse(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
