package com.study.service.impl;

import com.service.CodeService;
import com.study.dao.CodeDao;
import com.study.pojo.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;


public class LocalCodeServiceImpl implements CodeService {
    @Autowired
    private CodeDao codeDao;

    @Override
    public Boolean saveCode(String email, String code) {
        AuthCode authCode = new AuthCode();
        authCode.setEmail(email);
        authCode.setCode(code);

        Date createTime = new Date();
        Date expireTime = new Date(createTime.getTime() + 600000);
        authCode.setCreateTime(createTime);
        authCode.setExpireTime(expireTime);

        // 将随机码插入到数据库
        AuthCode saveCode = codeDao.save(authCode);

        Boolean isSave = false;
        if (saveCode != null){
            isSave = true;
        }
        return isSave;
    }

    @Override
    public AuthCode findByEmail(String email, String code) {
        AuthCode authCode = new AuthCode();
        authCode.setEmail(email);
        authCode.setCode(code);

        // 根据email查询用户
        Example<AuthCode> codeExample = Example.of(authCode);
        Optional<AuthCode> exampleResult = codeDao.findOne(codeExample);

        if (exampleResult.isPresent()){
            return exampleResult.get();
        }
        return null;
    }
}
