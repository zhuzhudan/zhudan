package com.study.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class IPConfig {
    @Value("${ipfilter.time}")
    private String time;
    @Value("${ipfilter.count}")
    private String count;

    public String getTime() {
        return time;
    }

    public String getCount() {
        return count;
    }
}
