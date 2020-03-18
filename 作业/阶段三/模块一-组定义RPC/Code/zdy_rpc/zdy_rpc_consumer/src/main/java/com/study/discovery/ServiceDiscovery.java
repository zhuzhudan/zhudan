package com.study.discovery;

/**
 * 服务发现，从注册中心获取
 */
public interface ServiceDiscovery {
    // 根据服务名称返回服务地址
    String discovery(String serviceName);
}
