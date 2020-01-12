package com.study.dao;

import com.study.pojo.User;

import java.util.List;

public interface IUserDao {

    // 查询所有用户
    public List<User> findAll() throws Exception;

    // 根据调剂进行用户查询
    public User findByCondition(User user) throws Exception;

    public void insert(User user);

    public void update(User user);

    public void delete(Integer id);
}
