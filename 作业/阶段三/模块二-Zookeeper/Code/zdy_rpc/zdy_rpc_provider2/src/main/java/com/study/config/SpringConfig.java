package com.study.config;

import com.study.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class SpringConfig {
    @Bean
    public RpcServer rpcRegistry(){
        return new RpcServer(getAddress(), 8989);
    }

    /**
     * 获得本机ip地址
     */
    private String getAddress(){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();
    }
}
