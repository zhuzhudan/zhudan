package com.study;

import com.study.proxy.RpcProxy;
import com.study.service.UserService;

public class ClientBootStrap {
    public static void main(String[] args) {
        UserService userService = RpcProxy.createProxy(UserService.class);
        System.out.println(userService.sayHello("are you ok?"));
    }
}
