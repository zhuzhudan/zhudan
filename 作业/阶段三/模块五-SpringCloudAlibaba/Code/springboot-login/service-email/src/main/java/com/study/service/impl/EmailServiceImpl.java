package com.study.service.impl;

import com.service.EmailService;
import com.study.bean.Mail;
import com.study.util.MailUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private MailUtil mailUtil;

    public Boolean sendCode(String email, String code){
        Mail mail = new Mail();
        mail.setToAccount(email);
        mail.setSubject("login 平台注册");
        mail.setContent("login 注册，验证码是：" + code);

        return mailUtil.sendMail(mail);
    }
}
