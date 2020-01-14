package com.springframework.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.springframework.annotation.Autowired;
import com.springframework.annotation.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 从当前线程获取连接
 */

public class ConnectionUtils {

    // 当前线程的连接
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public static Connection getCurrentThreadConn(DruidDataSource dataSource) throws SQLException {
        // 判断当前线程中是否已经绑定连接，如果没有绑定，需要从连接池获取一个连接绑定到当前线程
        Connection connection = threadLocal.get();
        if(connection == null){
            // 从连接池拿连接并绑定到线程
            connection = dataSource.getConnection();
            // 绑定到当前线程
            threadLocal.set(connection);
        }
        return connection;
    }

}
