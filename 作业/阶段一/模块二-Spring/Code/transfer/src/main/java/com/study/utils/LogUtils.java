package com.study.utils;

import com.springframework.annotation.Component;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Component
@Aspect
public class LogUtils {

    @Pointcut("execution(* com.study.service.impl.TransferServiceImpl.*(..))")
    public void pt1(){

    }

    /**
     * 业务逻辑开始执行之前执行
     */
    @Before("pt1()")
    public void beforeMethod(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            System.out.println(arg);
        }
        System.out.println("业务逻辑开始执行之前执行");
    }

    /**
     * 业务逻辑结束执行时执行（无论异常与否）
     */
    @After("pt1()")
    public void afterMethod(){
        System.out.println("业务逻辑结束时执行，无论异常与否");
    }

    /**
     * 业务逻辑异常时执行
     */
    @AfterThrowing("pt1()")
    public void exceptionMethod(){
        System.out.println("业务逻辑异常时执行");
    }

    /**
     * 业务逻辑正常结束执行时执行
     */
    @AfterReturning(value = "pt1()", returning = "retVal")
    public void successMethod(Object retVal){
        System.out.println("业务逻辑正常结束时执行");
    }

    /**
     * 业务逻辑正常结束执行时执行
     */
    @Around("pt1()")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("before method");
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        }catch (Exception e){
            System.out.println("exception method:"+ result);
        } finally {
            System.out.println("finally method");
        }
        System.out.println("after method");
        System.out.println("环绕通知");
        return result;
    }
}
