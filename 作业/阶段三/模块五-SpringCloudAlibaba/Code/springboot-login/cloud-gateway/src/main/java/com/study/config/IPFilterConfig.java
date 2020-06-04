package com.study.config;

import com.study.filter.IPFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class IPFilterConfig {

    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/user/register/**")
                        .filters(f -> f.filter(new IPFilter())
                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri("lb://service-user")
                        .order(0)
                        .id("service-user-ipfilter-router")
                )
                .build();
    }
}
