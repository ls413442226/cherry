package com.cherry.commons.tmplates;


import com.cherry.commons.properties.JwtUtilProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtilTemplate {

    private JwtUtilProperties jwtUtilProperties;

    public JwtUtilTemplate(JwtUtilProperties jwtUtilProperties) {
        this.jwtUtilProperties = jwtUtilProperties;
    }

    /**
     * 生成JWT
     *
     * @param id
     * @param subject
     * @param roles
     * @return
     */
    public String createJWT(String id, String subject, String roles) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder jwt = Jwts.builder().setId(id)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, jwtUtilProperties.getKey())
                .claim("roles", roles);
        if (jwtUtilProperties.getTtl() > 0) {
            jwt.setExpiration(new Date(nowMillis + jwtUtilProperties.getTtl()));
        }
        return jwt.compact();
    }

    /**
     * 解析JWT
     *
     * @param jwtStr
     * @return
     */
    public Claims parseJWT(String jwtStr) {
        return Jwts.parser()
                .setSigningKey(jwtUtilProperties.getKey())
                .parseClaimsJws(jwtStr)
                .getBody();
    }
}
