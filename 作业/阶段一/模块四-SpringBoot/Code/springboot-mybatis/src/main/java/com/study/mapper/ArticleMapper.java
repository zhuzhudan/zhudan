package com.study.mapper;

import com.study.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Select("select * from t_article")
    public List<Article> findAll();
}
