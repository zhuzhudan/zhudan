package com.study.springbootjpa;

import com.study.pojo.Article;
import com.study.respository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class SpringbootJpaApplicationTests {
    @Autowired
    ArticleRepository articleRepository;

    @Test
    void contextLoads() {
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            System.out.println(article);
        }

    }

    @Test
    void pageTest(){
        Pageable pageable  = PageRequest.of(0,3);
        Page<Article> all = articleRepository.findAll(pageable);
        List<Article> content = all.getContent();
        Pageable pageable1 = all.getPageable();

    }

}
