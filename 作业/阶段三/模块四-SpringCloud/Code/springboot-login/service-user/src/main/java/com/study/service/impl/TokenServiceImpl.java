package com.study.service.impl;

import com.study.dao.TokenDao;
import com.study.pojo.Token;
import com.study.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private TokenDao tokenDao;

    @Override
    public Boolean saveToken(String email, String tokenCode) {
        Token token = new Token();
        token.setEmail(email);
        token.setToken(tokenCode);

        // 插入用户
        Token saveToken = tokenDao.save(token);

        Boolean isSave = false;
        if (saveToken != null){
            isSave = true;
        }
        return isSave;
    }

    @Override
    public Token findTokenByCode(String tokenCode) {
        Token token = new Token();
        token.setToken(tokenCode);

        // 根据token查询
        Example<Token> tokenExample = Example.of(token);
        Optional<Token> exampleResult = tokenDao.findOne(tokenExample);

        if (exampleResult.isPresent()){
            return exampleResult.get();
        }
        return null;
    }
}
