package com.springframework.aop.aspect;

import com.springframework.aop.intercept.MethodInterceptor;
import com.springframework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AroundAdviceInterceptor extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public AroundAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("环绕之前");
        Object retVal = invocation.proceed();
        invocation.getMethod().invoke(invocation.getThis());
        System.out.println("环绕之后");
        return null;
    }
}
