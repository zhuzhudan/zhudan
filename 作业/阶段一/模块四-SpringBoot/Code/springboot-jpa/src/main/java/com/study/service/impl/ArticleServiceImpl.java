package com.study.service.impl;

import com.study.pojo.Article;
import com.study.respository.ArticleRepository;
import com.study.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public Page<Article> findAll(Integer pageNum, Integer pageSize) {
        Pageable pageable  = PageRequest.of(pageNum, pageSize);
        Page<Article> articlePage = articleRepository.findAll(pageable);
        return articlePage;
    }
}
