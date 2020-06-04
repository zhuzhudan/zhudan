package com.study.filter;

import com.study.config.IPConfig;
import com.study.config.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class IPFilter implements GatewayFilter, Ordered {
    private Map<String, List<Long>> ipMap = new ConcurrentHashMap<>();
    private List<String> blackList = new ArrayList<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        IPConfig ipConfig = (IPConfig) SpringUtil.getBean(IPConfig.class);

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 从request对象中获取客户端ip
        String host = request.getRemoteAddress().getHostString();

        if(blackList.contains(host)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 状态码
            String data = "请求次数过多";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            return response.writeWith(Mono.just(wrap));
        }

        List<Long> dates = null;
        if (ipMap.containsKey(host)){
            dates = ipMap.get(host);
        } else {
            dates = new ArrayList<>();
        }
        Long current = new Date().getTime();
        dates.add(current);

        Long millionSecond = Long.valueOf((Integer.parseInt(ipConfig.getTime()) * 60 * 1000));
        Integer count = Integer.valueOf(ipConfig.getCount());

        List<Long> collect = dates.stream().filter(f -> f < current - millionSecond).collect(Collectors.toList());
        dates.removeAll(collect);

        if (dates.size() >= count){
            blackList.add(host);
        }
        ipMap.put(host, dates);

        // 合法请求，放行，执行后续的过滤器
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
