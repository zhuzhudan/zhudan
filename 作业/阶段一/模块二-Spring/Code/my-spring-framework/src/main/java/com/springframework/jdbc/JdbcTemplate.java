package com.springframework.jdbc;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTemplate {
    private DruidDataSource dataSource;

    public JdbcTemplate(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PreparedStatement getPreparedStatement(String sql){
        Connection con = null;
        try {
            con = ConnectionUtils.getCurrentThreadConn(dataSource);
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            return preparedStatement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
