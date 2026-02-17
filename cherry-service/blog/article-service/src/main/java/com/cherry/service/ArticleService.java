package com.cherry.service;


import com.cherry.domain.blog.db.Article;

import java.util.List;

public interface ArticleService {

    List<Article> list();

    Article get(Long id);

    void add(Article article);

    void delete(Long id);

    void update(Article article);
}
