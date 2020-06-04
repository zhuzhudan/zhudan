package com.study.dao;

import com.study.pojo.Token;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;

@RefreshScope
public interface TokenDao extends JpaRepository<Token, Integer> {
}
