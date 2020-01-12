package com.study.sqlSession;

import com.study.config.BoundSql;
import com.study.pojo.Configuration;
import com.study.pojo.MappedStatement;
import com.study.utils.GenericTokenParser;
import com.study.utils.ParameterMapping;
import com.study.utils.ParameterMappingTokenHandler;
import com.study.utils.TokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// 第六步，创建Executor接口的实现类
// 底层执行的是JDBC代码
public class SimpleExecutor implements Executor {
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        // 1、注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2、获取sql语句
        // 转换sql语句，将#{id}，#{username}，转换成?占位符，
        // 转换过程中还需要对#{}里的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //3、获取预处理对象：preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //4、设置参数
        String parameterType = mappedStatement.getParameterType();//获取参数的全路径
        Class<?> parameterTypeClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //使用反射
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);

            preparedStatement.setObject(i + 1, o);
        }

        //5、执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        String resultType = mappedStatement.getResultType();
        Class<?> classType = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();

        //6、封装返回结果集
        while (resultSet.next()){
            Object o = classType.newInstance();
            //获取元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount() ; i++) {
                // 字段名，需要从1开始
                String columnName = metaData.getColumnName(i);
                // 字段值
                Object value = resultSet.getObject(columnName);

                //使用反射，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, classType);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }

        return (List<E>) objects;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object parameter) throws Exception {
        // 1、注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2、获取sql语句
        // 转换sql语句，将#{id}，#{username}，转换成?占位符，
        // 转换过程中还需要对#{}里的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //3、获取预处理对象：preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //4、设置参数
        String parameterType = mappedStatement.getParameterType();//获取参数的全路径
        Class<?> parameterTypeClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        if(parameterMappingList.size()==1){
            preparedStatement.setObject(1, parameter);
        } else {
            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();

                //使用反射
                Field declaredField = parameterTypeClass.getDeclaredField(content);
                declaredField.setAccessible(true);
                Object o = declaredField.get(parameter);

                preparedStatement.setObject(i + 1, o);
            }
        }
        //5、执行sql
        return preparedStatement.executeUpdate();
    }


    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if(parameterType != null){
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作：
     * 1、将#{}使用?代替
     * 2、解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        // 标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);

        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
