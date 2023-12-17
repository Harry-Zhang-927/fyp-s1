package com.example.demo.config;


import com.example.demo.interceptor.ConnectionInterceptor;
import com.example.demo.interceptor.MainInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
}
