package com.cherry.config;

import com.cherry.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Aaliyah
 */
/**
 * 安全配置类，用于配置Spring Security的相关策略和过滤器链。
 * 该类通过@EnableWebSecurity启用Web安全支持，并定义了认证和授权规则。
 * @author Aaliyah
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT认证过滤器，用于拦截请求并验证JWT令牌的有效性。
     */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 配置HTTP安全过滤器链。
     *
     * @param http HttpSecurity对象，用于构建安全配置
     * @return SecurityFilterChain 过滤器链对象
     * @throws Exception 配置过程中可能抛出的异常
     *
     * 主要配置内容包括：
     * 1. 禁用CSRF保护（适用于无状态API）
     * 2. 设置会话管理策略为无状态（STATELESS）
     * 3. 定义请求授权规则：
     *    - 允许匿名访问登录、注册和刷新令牌接口
     *    - 其他所有请求需要认证
     * 4. 在UsernamePasswordAuthenticationFilter之前添加JWT认证过滤器
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护，适用于RESTful API
                .csrf(AbstractHttpConfigurer::disable)

                // 设置会话管理策略为无状态
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问的路径
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
/**
 * 配置密码编码器，用于对用户密码进行加密处理。
 * 返回BCryptPasswordEncoder实例，确保密码以安全的哈希形式存储。
 *
 * @return PasswordEncoder BCryptPasswordEncoder实例
 */
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

/**
 * 配置认证管理器，用于处理用户身份验证逻辑。
 * 通过AuthenticationConfiguration获取默认的认证管理器实现。
 *
 * @param config AuthenticationConfiguration对象，用于构建认证管理器
 * @return AuthenticationManager 认证管理器实例
 * @throws Exception 配置过程中可能抛出的异常
 */
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}

}
