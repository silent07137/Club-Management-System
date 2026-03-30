package com.sil.club.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类 (适配 Spring Boot 3.x)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 使用最新的 Lambda 写法配置 Security
        http
            // 暂时关闭 CSRF 防护（前后端分离通常不需要）
            .csrf(AbstractHttpConfigurer::disable)
            // 配置请求权限拦截
            .authorizeHttpRequests(auth -> auth
                // 允许所有人直接访问 /test/ 下的所有接口
                .requestMatchers("/test/**").permitAll()
                // 暂时先把所有的接口都放行（等我们后面写了 JWT 拦截器，再把这里改成 authenticated()）
                .anyRequest().permitAll()
            );
            
        return http.build();
    }
}