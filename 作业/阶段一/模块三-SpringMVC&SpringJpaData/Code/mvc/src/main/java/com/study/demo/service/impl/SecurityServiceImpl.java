package com.study.demo.service.impl;

import com.study.demo.service.ISecurityService;
import com.study.mvcframework.annotations.MyService;

@MyService
public class SecurityServiceImpl implements ISecurityService {
    @Override
    public String get(String name) {
        System.out.println("security service 实现类中的name参数：" + name);
        return name;
    }
}
