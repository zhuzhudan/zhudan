package com.study.discovery;

import com.study.handler.RpcProxyHandler;
import com.study.netty.NettyClient;
import io.netty.channel.ChannelFuture;

/**
 * 服务发现，从注册中心获取
 */
public interface ServiceDiscovery {
    // 根据服务名称返回服务地址
    NettyClient discovery(String serviceName);

}
