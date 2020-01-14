package com.study.dao.impl;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Repository;
import com.springframework.jdbc.JdbcTemplate;
import com.study.dao.AccountDao;
import com.study.pojo.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class JdbcAccountDaoImpl implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void init(){
        System.out.println("初始化方法……");
    }

    public void destroy(){
        System.out.println("销毁方法……");
    }

    @Override
    public Account queryAccountByCardNo(String cardNo) throws Exception {

        String sql = "select * from account where cardNo=?";
        PreparedStatement preparedStatement = jdbcTemplate.getPreparedStatement(sql);
        preparedStatement.setString(1,cardNo);
        ResultSet resultSet = preparedStatement.executeQuery();

        Account account = new Account();
        while(resultSet.next()) {
            account.setCardNo(resultSet.getString("cardNo"));
            account.setName(resultSet.getString("name"));
            account.setMoney(resultSet.getInt("money"));
        }

        resultSet.close();
        preparedStatement.close();

        return account;
    }

    @Override
    public int updateAccountByCardNo(Account account) throws Exception {

//        Connection con = connectionUtils.getCurrentThreadConn();

        String sql = "update account set money=? where cardNo=?";
        PreparedStatement preparedStatement = jdbcTemplate.getPreparedStatement(sql);
        preparedStatement.setInt(1,account.getMoney());
        preparedStatement.setString(2,account.getCardNo());
        int i = preparedStatement.executeUpdate();

        preparedStatement.close();
        return i;
    }
}
