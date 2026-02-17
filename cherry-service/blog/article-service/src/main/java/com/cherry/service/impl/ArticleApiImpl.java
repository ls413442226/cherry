package com.cherry.service.impl;


import com.cherry.domain.blog.db.Article;
import com.cherry.mapper.ArticleMapper;
import com.cherry.service.ArticleService;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService(version = "1.0.0", group = "your-group")
public class ArticleApiImpl implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<Article> list() {
        return articleMapper.findAll();
    }

    @Override
    public Article get(Long id) {
        return articleMapper.findById(id);
    }

    @Override
    public void add(Article article) {
        articleMapper.insert(article);
    }

    @Override
    public void delete(Long id) {
        articleMapper.delete(id);
    }

    @Override
    public void update(Article article) {
        articleMapper.update(article);
    }
}
