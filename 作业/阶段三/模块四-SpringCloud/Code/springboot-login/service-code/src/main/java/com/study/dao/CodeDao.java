package com.study.dao;

import com.study.pojo.AuthCode;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;

@RefreshScope
public interface CodeDao extends JpaRepository<AuthCode, Integer> {
}
