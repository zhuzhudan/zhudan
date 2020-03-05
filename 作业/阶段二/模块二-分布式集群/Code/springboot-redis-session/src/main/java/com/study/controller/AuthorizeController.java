package com.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * 登录、退出的Controller
 */
@Controller
public class AuthorizeController {
    @RequestMapping(value = "/")
    public String homePage(){
        return "index";
    }

    @RequestMapping(value = "/login")
    public String login(ModelMap modelMap, HttpSession session, String name, String password){
        if (name != null && password != null){
            if(name.equals("admin") && password.equals("admin")) {
                // 如果账户密码正确，存入到session中，跳转到列表页
                session.setAttribute("username", name);
                session.setAttribute("flag", "success");
                return "redirect:/resume/query";
            } else {
                // 账户密码不正确，跳转到登录页面
                session.setAttribute("flag", "failed");
                modelMap.addAttribute("message", "用户没有权限，或密码错误");
                return "login";
            }
        } else {
            // 账户名、密码有未填写的
            Object string = session.getAttribute("flag");
            if (string != null && string.equals("failed"))
                modelMap.addAttribute("message", "请填写用户名和密码");
            return "login";
        }

    }

    // 退出登录
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.setAttribute("username", "");
        session.setAttribute("flag", "");
        return "login";
    }
}
