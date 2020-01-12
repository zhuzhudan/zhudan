package com.springframework.jdbc;


import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager {
    private DruidDataSource dataSource;

    public DataSourceTransactionManager(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    // 开启手动事务控制
    public void beginTransaction() throws SQLException {
        getCurrentThreadConn().setAutoCommit(false);
    }

    // 提交事务
    public void commit() throws SQLException {
        getCurrentThreadConn().commit();
    }

    // 回滚事务
    public void rollback() throws SQLException {
        getCurrentThreadConn().rollback();
    }
}
