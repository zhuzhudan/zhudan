package com.study.serializer;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * JSON 序列化
 */
public class JSONSerializer implements Serializer {
    public <T> byte[] serialize(T obj) throws IOException {
        return JSON.toJSONBytes(obj);
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return JSON.parseObject(bytes, clazz);
    }
}
