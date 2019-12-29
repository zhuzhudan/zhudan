package com.study.dao;

import com.study.io.Resources;
import com.study.pojo.User;
import com.study.sqlSession.SqlSession;
import com.study.sqlSession.SqlSessionFactory;
import com.study.sqlSession.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class UserDaoImpl implements IUserDao {
    // 1、代码重复

    @Override
    public List<User> findAll() throws Exception {
        // 1、获取文件流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        // 2、根据文件流，解析配置文件
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        // 3、拿到sqlSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
        List<User> users = sqlSession.selectList("user.selectList");
        for (User user1 : users) {
            System.out.println(user1);
        }

        return users;
    }

    @Override
    public User findByCondition(User user) throws Exception {
        // 1、获取文件流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        // 2、根据文件流，解析配置文件
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        // 3、拿到sqlSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
        User resultUser = sqlSession.selectOne("user.selectOne", user);
        System.out.println(resultUser);

        return resultUser;
    }

    @Override
    public void insert(User user) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Integer id) {

    }
}
