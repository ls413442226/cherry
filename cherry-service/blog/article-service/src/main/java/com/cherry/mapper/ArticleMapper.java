package com.cherry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cherry.domain.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    List<Article> findAll();

    Article findById(Long id);

    int insert(Article article);

    void delete(Long id);

    void update(Article article);
}
