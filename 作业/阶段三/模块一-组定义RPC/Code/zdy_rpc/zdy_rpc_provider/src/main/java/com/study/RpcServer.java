package com.study;

import com.study.codec.RpcDecoder;
import com.study.codec.RpcEncoder;
import com.study.handler.ProcessorHandler;
import com.study.protocol.RpcRequest;
import com.study.registry.RegistryCenter;
import com.study.registry.RegistryCenterWithZK;
import com.study.serializer.JSONSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class RpcServer implements ApplicationContextAware, InitializingBean {
    private String hostName;
    private int port;

    // 存储所有service的容器
    private Map<String, Object> registryServiceMap = new HashMap<>();

    // 注册中心（zookeeper）如果不使用注册中心依然可运行
    // private RegistryCenter registryCenter = new RegistryCenterWithZK();

    public RpcServer(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 默认的线程数就是CPU核心数*2
        // 主线程池，接收请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        // 子线程池，处理请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) //指定服务器端监听套接字通道NioServerSocketChannel
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    //设置业务责任链，channelHandler组成
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 使用JSON进行编、解码
                        pipeline.addLast(new RpcEncoder(String.class, new JSONSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));

                        // 实现自己的逻辑
                        pipeline.addLast(new ProcessorHandler(registryServiceMap));
                    }
                });

        // 正式启动服务
        serverBootstrap.bind(hostName, port).sync();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动网络连接
        start();
    }

    /**
     * 获取所有的 ServiceImpl
     * 将所有@Service class全部扫描出来，放到一个容器中
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取带有@Service的类
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(Service.class);
        if (!serviceBeanMap.isEmpty()) {
            // 遍历每一个ServiceImpl
            for (Object serviceBean : serviceBeanMap.values()) {
                // 获取Service的接口
                Class<?> serviceInterface = serviceBean.getClass().getInterfaces()[0];
                String serviceName = serviceInterface.getName();
                registryServiceMap.put(serviceName, serviceBean);


                // 如果有注册中心，需要进行注册
                // registryCenter.registry(serviceName, getAddress() + ":" + port);
            }
        }
    }

    /**
     * 获得本机ip地址
     * @return
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
