package com.study.service.impl;

import com.study.service.HelloService;
import org.apache.dubbo.rpc.RpcContext;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        String requestAddress = RpcContext.getContext().getAttachment("RequestAddress");
        System.out.println("request address: " + requestAddress);
        return "hello，" + name + " from provider-2 ";
    }
}
