package com.study.codec;

import com.study.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * RPC 解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> clazz;
    private Serializer serializer;

    public RpcDecoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readInt();
        byteBuf.markReaderIndex();
        if(byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
        } else {
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            Object deserialize = serializer.deserialize(bytes, clazz);
            list.add(deserialize);
        }
    }
}
