package com.study.handler;

import com.study.codec.RpcDecoder;
import com.study.codec.RpcEncoder;
import com.study.discovery.ServiceDiscovery;
import com.study.handler.RpcProxyHandler;
import com.study.netty.NettyClient;
import com.study.protocol.RpcRequest;
import com.study.serializer.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RemoteInvocationHandler implements InvocationHandler {
    // 注册服务发现
    private ServiceDiscovery serviceDiscovery;

    public RemoteInvocationHandler() {
    }

    public RemoteInvocationHandler(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            // 构造协议内容，消息
            RpcRequest request = new RpcRequest();
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            // 注册服务发现 获取服务的地址
            NettyClient nettyClient = serviceDiscovery.discovery(request.getClassName());
            ChannelPromise promise = nettyClient.getProxyHandler().sendRequest(request);
            promise.await();
            return nettyClient.getProxyHandler().getResponse();
        }

    }


}
