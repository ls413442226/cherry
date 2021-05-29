package com.cherry.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("cherry.jwt")
public class JwtUtilProperties {

    /**
     * 盐
     */
    private String key;
    /**
     * 超时时间
     */
    private long ttl;
}
