package com.study.serializer;

import java.io.IOException;

/**
 * 序列化接口
 */
public interface Serializer {
    /**
     * java对象转换为二进制，序列化
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 二进制转换为java对象，反序列化
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
