package com.study.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.springframework.annotation.Autowired;
import com.springframework.annotation.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 从当前线程获取连接
 */
@Component("connectionUtils")
public class ConnectionUtils {
    @Autowired
    private static DruidDataSource dataSource = new DruidDataSource();



    // 当前线程的连接
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public Connection getCurrentThreadConn() throws SQLException {
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
