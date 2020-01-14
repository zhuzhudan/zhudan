package com.springframework.jdbc;


import com.alibaba.druid.pool.DruidDataSource;
import com.springframework.aop.aspect.JoinPoint;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager {
    private DruidDataSource dataSource;

    public DataSourceTransactionManager(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 开启手动事务控制
    public void beginTransaction(JoinPoint joinPoint) {
        try {
            ConnectionUtils.getCurrentThreadConn(dataSource).setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 提交事务
    public void commit(JoinPoint joinPoint) {
        try {
            ConnectionUtils.getCurrentThreadConn(dataSource).commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 回滚事务
    public void rollback(JoinPoint joinPoint, Throwable ex)  {
        try {
            ConnectionUtils.getCurrentThreadConn(dataSource).rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.err.println("事务中出现异常："+ex.getMessage());
    }


}
