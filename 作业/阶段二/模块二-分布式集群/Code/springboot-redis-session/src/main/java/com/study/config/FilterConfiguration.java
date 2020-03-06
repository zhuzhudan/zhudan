package com.study.config;

import com.study.filter.SessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfiguration {
    @Autowired
    private SessionFilter sessionFilter;

    @Bean
    public FilterRegistrationBean registerFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(sessionFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("session-filter");
        return registrationBean;
    }
}
