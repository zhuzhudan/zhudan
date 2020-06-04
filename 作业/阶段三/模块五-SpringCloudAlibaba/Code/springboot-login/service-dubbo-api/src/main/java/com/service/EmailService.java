package com.service;

public interface EmailService {
    Boolean sendCode(String email, String code);
}
