package com.study.service;

import com.study.pojo.Article;
import org.springframework.data.domain.Page;

public interface ArticleService {
    Page<Article> findAll(Integer pageNum, Integer pageSize);
}
