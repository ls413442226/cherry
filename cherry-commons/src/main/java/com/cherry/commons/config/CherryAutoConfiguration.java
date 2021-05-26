package com.cherry.commons.config;


import com.cherry.commons.properties.SmsProperties;
import com.cherry.commons.tmplates.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//自动读取yml中的配置信息,并复制到SmsProperties对象,将此对象存入容器
@EnableConfigurationProperties({SmsProperties.class})
public class CherryAutoConfiguration {
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        return new SmsTemplate(smsProperties);
    }
}
