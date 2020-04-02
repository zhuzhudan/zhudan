package com.study.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String word) {
        System.out.println("调用成功--参数：" + word);
        return "success";
    }
}
