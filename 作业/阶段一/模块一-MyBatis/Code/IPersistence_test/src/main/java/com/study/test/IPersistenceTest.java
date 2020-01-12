package com.study.test;

import com.study.dao.IUserDao;
import com.study.io.Resources;
import com.study.pojo.User;
import com.study.sqlSession.SqlSession;
import com.study.sqlSession.SqlSessionFactory;
import com.study.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {
    private SqlSession sqlSession;

    @Before
    public void before() throws PropertyVetoException, DocumentException {
        // 1、获取文件流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        // 2、根据文件流，解析配置文件
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        // 3、拿到sqlSession对象
        sqlSession = sqlSessionFactory.openSession();
    }

    @Test
    public void test() throws Exception {
        //调用
        User user = new User();
        user.setId(1);
        user.setUsername("lisi");
//        Object resultUser = sqlSession.selectOne("com.study.dao.IUserDao.findByCondition", user);
//        System.out.println(resultUser);

//        List<User> users = sqlSession.selectList("user.selectList");
//        for (User user1 : users) {
//            System.out.println(user1);
//        }

        //
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);// 返回代理对象
//        List<User> all = userDao.findAll();
//        for (User user1 : all) {
//            System.out.println(user1);
//        }
        User byCondition = userDao.findByCondition(user);
        System.out.println(byCondition);


    }

    @Test
    public void insertTest() throws Exception {
        User user = new User();
        user.setId(4);
        user.setUsername("tom");
//        Object resultUser = sqlSession.insert("com.study.dao.IUserDao.insert", user);
//        System.out.println(resultUser);
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);// 返回代理对象
        userDao.insert(user);
    }

    @Test
    public void updateTest() throws Exception {
        User user = new User();
        user.setId(4);
        user.setUsername("jerry");
//        Object resultUser = sqlSession.update("com.study.dao.IUserDao.update", user);
//        System.out.println(resultUser);
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);// 返回代理对象
        userDao.update(user);
    }

    @Test
    public void deleteTest() throws Exception {
//        Object resultUser = sqlSession.delete("com.study.dao.IUserDao.delete", 4);
//        System.out.println(resultUser);
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);// 返回代理对象
        userDao.delete(4);
    }
}
