package com.study;

import com.study.proxy.RpcProxy;
import com.study.service.UserService;

public class ClientBootStrap {
    public static void main(String[] args) throws InterruptedException {
        UserService userService = RpcProxy.createProxy(UserService.class);
        System.out.println(userService.sayHello("1、are you ok?"));
        System.out.println("可关闭任一服务端");
        Thread.sleep(6000);

        System.out.println(userService.sayHello("2、are you ok?"));
        System.out.println("可开启刚刚关闭的服务端");
        Thread.sleep(60000);

        System.out.println(userService.sayHello("3、are you ok?"));
        System.out.println(userService.sayHello("4、are you ok?"));

        Thread.sleep(Integer.MAX_VALUE);
    }
}
