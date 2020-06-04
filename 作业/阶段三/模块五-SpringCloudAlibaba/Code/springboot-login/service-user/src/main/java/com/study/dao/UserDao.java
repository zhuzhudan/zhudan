package com.study.dao;

import com.study.pojo.User;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;

@RefreshScope
public interface UserDao extends JpaRepository<User, Integer> {
}
