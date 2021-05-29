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
}
