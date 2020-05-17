package com.study.controller;

import com.study.bean.Mail;
import com.study.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private MailUtil mailUtil;

    /**
     * 发送验证码到邮箱
     * @return true成功，false失败
     */
    @GetMapping("/{email}/{code}")
    public Boolean sendCode(@PathVariable(name = "email") String email,
                            @PathVariable(name = "code") String code){
        Mail mail = new Mail();
        mail.setToAccount(email);
        mail.setSubject("login 平台注册");
        mail.setContent("login 注册，验证码是：" + code);

        return mailUtil.sendMail(mail);
    }

}
