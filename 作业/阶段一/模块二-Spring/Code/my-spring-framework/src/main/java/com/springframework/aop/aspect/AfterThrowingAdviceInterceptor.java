package com.springframework.aop.aspect;

import com.springframework.aop.intercept.MethodInterceptor;
import com.springframework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AfterThrowingAdviceInterceptor extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private String throwingName;

    public AfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e){
            invokeAdviceMethod(invocation, null, e.getCause());
            throw e;
        }

    }

    public void setThrowingName(String throwingName){
        this.throwingName = throwingName;
    }
}
