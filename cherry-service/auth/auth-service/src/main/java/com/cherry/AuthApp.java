package com.cherry;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Aaliyah
 */
@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = "com.cherry.mapper", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class AuthApp {
    public static void main(String[] args) {
        SpringApplication.run(AuthApp.class, args);
        System.out.println("启动成功");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("临时密码："+encoder.encode("123456"));
    }
}