package com.cherry.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    // controller接口所在的包
    private String basePackage;
    // 当前文档的标题
    private String title;
    // 当前文档的详细描述
    private String description;
    // 当前文档的版本
    private String version;
}
