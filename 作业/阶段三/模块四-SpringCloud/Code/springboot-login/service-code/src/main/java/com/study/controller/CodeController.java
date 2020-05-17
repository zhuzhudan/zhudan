package com.study.controller;

import com.study.controller.service.EmailServiceFeignClient;
import com.study.pojo.AuthCode;
import com.study.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController
@RequestMapping("/api/code")
public class CodeController {
    @Autowired
    private CodeService codeService;

    @Autowired
    private EmailServiceFeignClient emailServiceFeignClient;

    /**
     * ⽣成验证码并发送到对应邮箱
     * @return 成功true，失败false
     */
    @GetMapping("/create/{email}")
    public Boolean create(@PathVariable(name = "email") String email, HttpSession session){

        // 生成随机六位
        String random = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        Boolean isSave = codeService.saveCode(email, random);
        if (!isSave){
            return false;
        }
        // 发送邮件
        Boolean isSend = emailServiceFeignClient.sendCode(email, random);
        if (isSend){
            session.setAttribute("message", "验证码已发送到用户");
        } else {
            session.setAttribute("message", "验证码发送失败，请检查邮箱是否正确");
        }

        return true;
    }

    /**
     * 校验验证码是否正确
     * @return 0:正确 1:错误 2:超时
     */
    @GetMapping("/validate/{email}/{code}")
    public Integer validate(@PathVariable(name = "email") String email,
                            @PathVariable(name = "code") String code){
        AuthCode authCode = codeService.findByEmail(email, code);
        if (authCode == null){
            return 1;
        }
        Date now = new Date();
        Long diff = now.getTime() - authCode.getExpireTime().getTime();
        if (diff > 0){
            return 2;
        }
        return 0;
    }
}
