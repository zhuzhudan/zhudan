package com.study.service.impl;

import com.study.mapper.ArticleMapper;
import com.study.pojo.Article;
import com.study.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<Article> findAll() {
        return articleMapper.findAll();
    }
}
