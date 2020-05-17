package com.study.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {
    @Bean
    public MailUtil getMailUtil(){
        return new MailUtil();
    }
}
