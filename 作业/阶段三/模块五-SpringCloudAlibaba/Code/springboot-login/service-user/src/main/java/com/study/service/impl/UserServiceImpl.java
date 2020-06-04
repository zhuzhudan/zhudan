package com.study.service.impl;

import com.study.dao.UserDao;
import com.study.pojo.User;
import com.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;


    @Override
    public Boolean saveUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        // 插入用户
        User saveUser = userDao.save(user);

        Boolean isSave = false;
        if (saveUser != null){
            isSave = true;
        }
        return isSave;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = new User();
        user.setEmail(email);

        // 根据email查询用户
        Example<User> userExample = Example.of(user);
        Optional<User> exampleResult = userDao.findOne(userExample);

        if (exampleResult.isPresent()){
            return exampleResult.get();
        }
        return null;
    }
}
