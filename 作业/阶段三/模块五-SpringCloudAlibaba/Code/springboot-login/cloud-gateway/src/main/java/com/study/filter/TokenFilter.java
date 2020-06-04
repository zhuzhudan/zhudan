package com.study.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TokenFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 获取请求路径
        RequestPath path = request.getPath();
        String string = path.subPath(0, 6).toString();
        if (string.equals("/api/user/login") ||
            string.equals("/api/user/register") ||
            string.equals("/api/user/isRegistered")){
            return chain.filter(exchange);
        }

        String[] paths = path.toString().split("/");
        String token = paths[paths.length - 1];
        //重定向(redirect)到登录页面
        if (StringUtils.isBlank(token)) {
            String url = "http://www.test.com/static/login.html";
            response = exchange.getResponse();
            //303状态码表示由于请求对应的资源存在着另一个URI，应使用GET方法定向获取请求的资源
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION, url);
            return response.setComplete();
        }
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
