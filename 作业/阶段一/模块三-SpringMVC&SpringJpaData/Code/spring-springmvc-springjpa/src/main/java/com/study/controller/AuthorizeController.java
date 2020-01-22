package com.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class AuthorizeController {

    @RequestMapping(value = "/login")
    public String login(ModelMap modelMap, HttpSession session, String name, String password){
        if (name != null && password != null){
            if(name.equals("admin") && password.equals("admin")) {
                session.setAttribute("username", name);
                session.setAttribute("flag", "success");
                return "redirect:/resume/query";
            } else {
                session.setAttribute("flag", "failed");
                modelMap.addAttribute("message", "用户没有权限，或密码错误");
                return "login";
            }
        } else {
            Object string = session.getAttribute("flag");
            if (string != null && string.equals("failed"))
                modelMap.addAttribute("message", "请填写用户名和密码");
            return "login";
        }

    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.setAttribute("username", "");
        session.setAttribute("flag", "");
        return "login";
    }


}
