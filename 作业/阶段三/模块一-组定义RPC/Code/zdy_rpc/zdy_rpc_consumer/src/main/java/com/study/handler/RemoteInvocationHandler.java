package com.study.handler;

import com.study.codec.RpcDecoder;
import com.study.codec.RpcEncoder;
import com.study.discovery.ServiceDiscovery;
import com.study.handler.RpcProxyHandler;
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
            // String serviceAddress = serviceDiscovery.discovery(request.getClassName());
            // String[] urls = serviceAddress.split(":");

            final RpcProxyHandler proxyHandler = new RpcProxyHandler();

            // 发送网络请求
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            try {
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                                pipeline.addLast(new RpcDecoder(String.class, new JSONSerializer()));
                                pipeline.addLast(proxyHandler);
                            }
                        });
                ChannelFuture future = bootstrap.connect(getAddress(), 8990).sync();
                // ChannelFuture future = bootstrap.connect(urls[0], Integer.parseInt(urls[1])).sync();
                future.channel().writeAndFlush(request).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
            return proxyHandler.getResponse();
        }

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
