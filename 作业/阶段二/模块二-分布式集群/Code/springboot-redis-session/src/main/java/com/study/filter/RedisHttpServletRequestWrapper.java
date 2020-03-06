package com.study.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.*;

public class RedisHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private HttpSession session;
    private HttpServletResponse response;
    private RedisTemplate redisTemplate;

    public RedisHttpServletRequestWrapper(HttpServletRequest request, HttpServletResponse response, RedisTemplate redisTemplate) {
        super(request);
        this.response = response;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public HttpSession getSession() {
        if (session != null){
            return session;
        }
        String id = "redisSession" + System.currentTimeMillis();
        writeSessionIdToCookie(id);
        session = new RedisHttpSession(null, id, redisTemplate);
        return session;
    }

    public void writeSessionIdToCookie(String id){
        Cookie cookie = new Cookie("redisSession", id);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
