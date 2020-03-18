package com.study.proxy;

import com.study.discovery.ServiceDiscovery;
import com.study.discovery.ServiceDiscoveryWithZK;
import com.study.handler.RemoteInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * 创建代理
 */
public class RpcProxy {
    // 服务发现
    // private static ServiceDiscovery serviceDiscovery = new ServiceDiscoveryWithZK();

    public static <T> T createProxy(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RemoteInvocationHandler());
        // return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RemoteInvocationHandler(serviceDiscovery));
    }
 }
