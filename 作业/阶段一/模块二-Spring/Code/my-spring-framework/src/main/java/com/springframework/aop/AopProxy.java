package com.springframework.aop;

public interface AopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
