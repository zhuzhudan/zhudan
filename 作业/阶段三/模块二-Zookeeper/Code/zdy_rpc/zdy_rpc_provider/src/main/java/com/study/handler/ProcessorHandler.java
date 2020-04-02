package com.study.handler;

import com.study.protocol.RpcRequest;
import com.study.registry.RegistryCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * pipeline中实现自己的逻辑，业务逻辑
 */
public class ProcessorHandler extends ChannelInboundHandlerAdapter {
    private String hostName;
    private int port;
    private Map<String, Object> registryServiceMap;
    RegistryCenter registryCenter;

    public ProcessorHandler(String hostName, int port, Map<String, Object> registryServiceMap, RegistryCenter registryCenter) {
        this.hostName = hostName;
        this.port = port;
        this.registryServiceMap = registryServiceMap;
        this.registryCenter = registryCenter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long start = System.nanoTime();
        // 1、当有客户端连接过来之后，就会获取协议内容 RpcRequest 的对象
        RpcRequest request = (RpcRequest) msg;

        Object result = new Object();

        // 2、要去注册好的容器中找到符合条件服务，进行反射调用
        String serviceName = request.getClassName();
        if (registryServiceMap.containsKey(serviceName)) {
            // 从容器中获取ServiceImpl
            Object service = registryServiceMap.get(serviceName);
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            result = method.invoke(service, request.getParameters());
        } else {
            throw new RuntimeException("service not found: " + serviceName);
        }

        // 3、通过远程调用Provider得到返回结果，并返回给客户端
        ctx.write(result);
        ctx.flush();

        long end = System.nanoTime();
        registryCenter.setServerResponseTime(String.valueOf(end - start), "/" + serviceName + "/" + hostName + ":" + port);
    }
}
