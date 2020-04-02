package com.study.netty;

import com.study.codec.RpcDecoder;
import com.study.codec.RpcEncoder;
import com.study.handler.RpcProxyHandler;
import com.study.protocol.RpcRequest;
import com.study.serializer.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端与服务端连接
 */
public class NettyClient {
    // 主机地址
    String host;
    // 端口号
    Integer port;

    EventLoopGroup group;
    RpcProxyHandler proxyHandler;
    ChannelFuture future;

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 创建连接
     */
    public void connect(){
        group = new NioEventLoopGroup();
        proxyHandler = new RpcProxyHandler();

        Bootstrap bootstrap = new Bootstrap();
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

        try {
            future = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(host + ":"+port+" 建立连接");
    }

    public RpcProxyHandler getProxyHandler() {
        return proxyHandler;
    }

    public ChannelFuture getFuture() {
        return future;
    }

    /**
     * 关闭连接
     */
    public void closeClient(){
        group.shutdownGracefully();
        System.out.println(host + ":"+port+" 断开连接");
    }
}
