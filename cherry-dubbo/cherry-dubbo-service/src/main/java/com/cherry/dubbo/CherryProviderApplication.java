package com.cherry.dubbo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cherry.dubbo.mapper")
public class CherryProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CherryProviderApplication.class,args);
    }
}
