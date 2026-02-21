package com.cherry;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Aaliyah
 */
@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = "com.cherry.mapper", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class ArticleApp {
    public static void main(String[] args) {
        SpringApplication.run(ArticleApp.class,args);
    }
}