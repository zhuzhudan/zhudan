package com.study.service;

import com.study.pojo.Token;

public interface TokenService {
    Boolean saveToken(String email, String tokenCode);

    Token findTokenByCode(String tokenCode);
}
