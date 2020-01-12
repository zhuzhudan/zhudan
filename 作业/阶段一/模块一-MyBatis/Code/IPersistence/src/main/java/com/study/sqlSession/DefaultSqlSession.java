package com.study.sqlSession;

import com.study.pojo.Configuration;
import com.study.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

// 第五步，创建SqlSession的实现类，定义对数据库的操作CRUD
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        // 将要完成对 simpleExecutor 里的query方法的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if(objects.size() == 1){
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空，或返回结果过多");
        }

    }



    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理，为Dao接口生成代理对象，并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {

            //proxy：当前代理对象的应用
            //method：当前被调用方法的引用
            //args：传递的参数
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //底层还是执行jdbc方法
                //根据不同情况，来调用selectList或者selectOne

                //准备参数1：statementId = namespace.id = 接口全限定名.方法名
                String methodName = method.getName(); // methodName=findAll
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                Map<String, MappedStatement> mappedStatementMap = configuration.getMappedStatementMap();
                MappedStatement mappedStatement = mappedStatementMap.get(statementId);
                Object resultObject = null;
                if(mappedStatement != null) {
                    String commandType = mappedStatement.getCommandType();
                    switch (commandType) {
                        case "select":
                            //准备参数2：params = args

                            //获取被调用方法的返回类型
                            Type genericReturnType = method.getGenericReturnType();
                            //判断是否进行了 泛型类型参数化
                            if (genericReturnType instanceof ParameterizedType) {
                                List<Object> objects = selectList(statementId, args);
                                resultObject = objects;
                            }else {

                                resultObject = selectOne(statementId, args);
                            }
                            break;
                        case "insert":
                            resultObject = insert(statementId, args[0]);
                            break;

                        case "update":
                            resultObject = update(statementId, args[0]);
                            break;

                        case "delete":
                            resultObject = delete(statementId,args[0]);
                            break;
                    }
                }else{
                    throw new RuntimeException("Error");
                }
                return resultObject;
            }
        });

        return (T) proxyInstance;
    }

    @Override
    public int insert(String statementId, Object params) throws Exception {
        return update(statementId, params);
    }

    @Override
    public int update(String statementId, Object params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(configuration, mappedStatement, params);
    }

    @Override
    public int delete(String statementId, Object params) throws Exception {
        return update(statementId, params);
    }

}
