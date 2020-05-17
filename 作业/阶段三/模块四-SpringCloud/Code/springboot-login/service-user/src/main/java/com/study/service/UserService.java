package com.study.service;

import com.study.pojo.User;

public interface UserService {
    Boolean saveUser(String email, String password);

    User findUserByEmail(String email);
}
