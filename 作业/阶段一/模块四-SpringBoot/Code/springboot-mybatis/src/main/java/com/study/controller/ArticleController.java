package com.study.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.pojo.Article;
import com.study.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleService.findAll();
        PageInfo<Article> articlePageInfo = new PageInfo<>(articles);
        model.addAttribute("articles", articles);
        model.addAttribute("pageInfo", articlePageInfo);
        return "index";

    }
}
