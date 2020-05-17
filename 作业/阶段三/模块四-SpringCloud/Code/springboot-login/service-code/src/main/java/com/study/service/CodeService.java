package com.study.service;

import com.study.pojo.AuthCode;

import java.util.Date;

public interface CodeService {
    Boolean saveCode(String email, String code);

    AuthCode findByEmail(String email, String Code);
}
