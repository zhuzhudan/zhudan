package com.study.demo.controller;

import com.study.demo.service.ISecurityService;
import com.study.mvcframework.annotations.MyAutowired;
import com.study.mvcframework.annotations.MyController;
import com.study.mvcframework.annotations.MyRequestMapping;
import com.study.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
@MyRequestMapping("/security")
@Security({"zhangsan", "lisi"})
public class SecurityController {
    @MyAutowired
    private ISecurityService securityService;


    @MyRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
        String string = securityService.get(username);
        response.getWriter().write("hello, /security/query: "+string);
        return;
    }

    @MyRequestMapping("/queryByUser")
    @Security("lisi")
    public void queryByUser(HttpServletRequest request, HttpServletResponse response, String username){
        String string = securityService.get(username);
        try {
            response.getWriter().write("hello, @Security: /security/queryByUser: "+string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @MyRequestMapping("/queryByUsers")
    @Security({"zhangsan", "lisi", "wangwu"})
    public void queryByUsers(HttpServletRequest request, HttpServletResponse response, String username){
        String string = securityService.get(username);
        try {
            response.getWriter().write("hello, @Security: /security/queryByUsers: "+string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
