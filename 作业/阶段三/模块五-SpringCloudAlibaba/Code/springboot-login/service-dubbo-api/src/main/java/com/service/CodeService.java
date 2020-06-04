package com.service;

import com.study.pojo.AuthCode;

public interface CodeService {
    Boolean saveCode(String email, String code);

    AuthCode findByEmail(String email, String Code);
}
