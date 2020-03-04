package com.study;

import com.study.interceptor.AuthorizeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 进行MVC的拦截器Interceptor配置，验证是否登录
 */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
    @Autowired
    private AuthorizeInterceptor authorizeInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizeInterceptor).addPathPatterns("/**").
                excludePathPatterns("/").excludePathPatterns("/login");
    }
}
