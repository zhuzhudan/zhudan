package com.study.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthorizeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        System.out.println(url);
        // 判断是否是登录页面
        if(url != null && url.indexOf("login") >= 0){
            // 是则放行
            return true;
        }

        // 其他页面，则需要判断是否已经登录，登录放行，否则去登录
        HttpSession session = request.getSession();
        Object userName = session.getAttribute("username");
        Object string = session.getAttribute("flag");

        if (userName != null && userName.equals("admin") && string != null && string.equals("success")){
            return true;
        }

        response.sendRedirect("/login");

        return false;
    }
}
