package com.cherry.commons.configuration;


import com.cherry.commons.properties.JwtUtilProperties;
import com.cherry.commons.properties.SmsProperties;
import com.cherry.commons.properties.SwaggerProperties;
import com.cherry.commons.tmplates.JwtUtilTemplate;
import com.cherry.commons.tmplates.SmsTemplate;
import com.cherry.commons.tmplates.SwaggerTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//自动读取yml中的配置信息,并复制到SmsProperties对象,将此对象存入容器
@EnableConfigurationProperties({SmsProperties.class,SwaggerProperties.class, JwtUtilProperties.class})
public class CherryAutoConfiguration {
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        return new SmsTemplate(smsProperties);
    }

    @Bean
    public SwaggerTemplate swaggerTemplate(SwaggerProperties swaggerProperties){
        return new SwaggerTemplate(swaggerProperties);
    }

    @Bean
    public JwtUtilTemplate jwtUtilTemplate(JwtUtilProperties jwtUtilProperties){
        return new JwtUtilTemplate(jwtUtilProperties);
    }

    @Bean
    public RedisTemplate<String, Object> empRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
