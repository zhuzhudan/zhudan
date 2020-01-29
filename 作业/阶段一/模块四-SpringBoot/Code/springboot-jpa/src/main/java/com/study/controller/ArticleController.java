package com.study.controller;

import com.study.pojo.Article;
import com.study.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/")
    public String index(){
        return "redirect:/list";
    }

    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize){
        Page<Article> articlePage = articleService.findAll(pageNum, pageSize);
        model.addAttribute("articlePage", articlePage);
        return "index";
    }

}
