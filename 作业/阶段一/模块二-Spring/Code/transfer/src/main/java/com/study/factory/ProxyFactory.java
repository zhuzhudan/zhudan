package com.study.factory;

import com.springframework.annotation.Component;
import com.study.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component("proxyFactory")
public class ProxyFactory {
    @Autowired
    private TransactionManager transactionManager;

    /**
     * JDK 动态代理
     */
    public Object getJdkProxy(Object obj){
        // 获取代理对象
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        try {
                            // 开启事务(关闭事务的自动提交)
//                            TransactionManager.getInstance().beginTransaction();
                            transactionManager.beginTransaction();

                            result = method.invoke(obj, args);

                            // 提交事务
//                            TransactionManager.getInstance().commit();
                            transactionManager.commit();
                        } catch (Exception e){
                            // 回滚事务
//                            TransactionManager.getInstance().rollback();
                            transactionManager.rollback();
                            // 抛出异常便于上层servlet捕获
                            throw e;
                        }
                        return result;
                    }
                });
    }

    /**
     * CGLib动态代理
     */
    public Object getCglibProxy(Object obj){
        return Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try {
                    // 开启事务(关闭事务的自动提交)
//                    TransactionManager.getInstance().beginTransaction();
                    transactionManager.beginTransaction();

                    result = method.invoke(obj, objects);

                    // 提交事务
//                    TransactionManager.getInstance().commit();
                    transactionManager.commit();
                } catch (Exception e){
                    // 回滚事务
//                    TransactionManager.getInstance().rollback();
                    transactionManager.rollback();
                    // 抛出异常便于上层servlet捕获
                    throw e;
                }
                return result;
            }
        });
    }
}
