package com.study.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class RedisHttpSession extends RedisHttpSessionWrapper {
    private RedisTemplate redisTemplate;
    private String id;
    private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    public RedisHttpSession(HttpSession session, String id, RedisTemplate redisTemplate) {
        super(session);
        this.id = id;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(String s) {
        byte[] key = stringRedisSerializer.serialize(s);
        Object value = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.get(key);
            }
        });
        return value;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        byte[] key = stringRedisSerializer.serialize(id);
        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Set<byte[]> set = redisConnection.keys(key);
                return set;
            }
        });
        if(result != null){
            Set<byte[]> s = (Set<byte[]>) result;
            Set<String> ss = new HashSet<String>();
            for(byte[] b : s) {
                ss.add(stringRedisSerializer.deserialize(b));
            }
            Enumeration<String> en = new Vector(ss).elements();
            return en;
        }
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {
        final byte[] key = stringRedisSerializer.serialize(s);
        final byte[] v = stringRedisSerializer.serialize((String)o);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                connection.set(key, v);
                return null;
            }
        });
    }
}
