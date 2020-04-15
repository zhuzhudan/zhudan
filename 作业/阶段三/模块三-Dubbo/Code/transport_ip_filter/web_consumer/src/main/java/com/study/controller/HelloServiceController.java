package com.study.controller;

import com.study.service.HelloService;
import com.study.util.IpUtil;
import com.study.util.RequestHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloServiceController {
    @Reference
    private HelloService helloService;

    @RequestMapping("/")
    public Object index(HttpServletRequest request){
        // 将ip与threalocal进行绑定
        RequestHolder.setRequest(IpUtil.getIpAddr(request));
        return helloService.sayHello("zhudan");
    }
}
