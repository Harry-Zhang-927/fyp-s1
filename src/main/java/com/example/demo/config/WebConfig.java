package com.example.demo.config;


import com.example.demo.interceptor.ConnectionInterceptor;
import com.example.demo.interceptor.MainInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ConnectionInterceptor connectionInterceptor;

    @Autowired
    private MainInterceptor mainInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(connectionInterceptor).addPathPatterns("/connect/**");;
        registry.addInterceptor(mainInterceptor).addPathPatterns("/**");;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 前端服务器地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 明确包括 OPTIONS
                .allowCredentials(true) // 允许携带认证信息
                .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers") // 明确指定允许的头部
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials") // 显式暴露所需头部
                .maxAge(3600); // 预检请求的缓存时间
    }
}
