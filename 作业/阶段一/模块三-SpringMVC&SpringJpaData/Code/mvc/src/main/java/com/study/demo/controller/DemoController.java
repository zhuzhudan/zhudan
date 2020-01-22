package com.study.demo.controller;

import com.study.demo.service.IDemoService;
import com.study.mvcframework.annotations.MyAutowired;
import com.study.mvcframework.annotations.MyController;
import com.study.mvcframework.annotations.MyRequestMapping;
import com.study.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;

@MyController
@MyRequestMapping("/demo")
public class DemoController {

    @MyAutowired
    private IDemoService demoService;

    @MyRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
        String string = demoService.get(username);
        response.getWriter().write("hello, /demo/query: "+string);
        return;
    }

    @MyRequestMapping("/queryByUser")
    @Security("lisi")
    public void queryByUser(HttpServletRequest request, HttpServletResponse response, String username){
        String string = demoService.get(username);
        try {
            response.getWriter().write("hello, @Security: /demo/queryByUser: "+string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @MyRequestMapping("/queryByUsers")
    @Security({"zhangsan", "lisi"})
    public void queryByUsers(HttpServletRequest request, HttpServletResponse response, String username){
        String string = demoService.get(username);
        try {
            response.getWriter().write("hello, @Security: /demo/queryByUser: "+string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
