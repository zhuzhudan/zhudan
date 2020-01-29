package com.study.springbootmybatis;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.mapper.ArticleMapper;
import com.study.pojo.Article;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class SpringbootMybatisApplicationTests {

    @Autowired
    private ArticleMapper articleMapper;

    @Test
    void contextLoads() {
        List<Article> articles = articleMapper.findAll();
        for (Article article : articles) {
            System.out.println(article);
        }
    }

    @Test
    public void pagehelperTest(){
        PageHelper.startPage(1, 3);
        List<Article> articles = articleMapper.findAll();
        PageInfo<Article> articlePageInfo = new PageInfo<>(articles);
        System.out.println("总条数：" + articlePageInfo.getTotal());
        System.out.println("总页数：" + articlePageInfo.getPages());
        System.out.println("当前页：" + articlePageInfo.getPageNum());
        System.out.println("每页显示长度：" + articlePageInfo.getPageSize());
        System.out.println("是否第一页：" + articlePageInfo.isIsFirstPage());
        System.out.println("是否最后一页：" + articlePageInfo.isIsLastPage());
    }

}
