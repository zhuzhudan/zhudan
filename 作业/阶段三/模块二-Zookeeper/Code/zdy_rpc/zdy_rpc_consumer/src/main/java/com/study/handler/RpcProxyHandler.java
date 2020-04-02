package com.study.handler;

import com.study.protocol.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RpcProxyHandler extends ChannelInboundHandlerAdapter {
    private Object response;
    // 同步获取结果
    private ChannelPromise promise;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
    }

    public ChannelPromise sendRequest(RpcRequest request){
        if (ctx == null)
            throw new IllegalStateException();
        promise = ctx.writeAndFlush(request).channel().newPromise();
        return promise;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
        promise.setSuccess();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception:");
        ctx.close();
    }


    public Object getResponse(){
        return response;
    }
}
