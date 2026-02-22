package com.cherry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author Aaliyah
 */
@Configuration
public class RedisLuaConfig {

    @Bean
    public DefaultRedisScript<Long> refreshCheckScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/refresh_check.lua"));
        script.setResultType(Long.class);
        return script;
    }
}