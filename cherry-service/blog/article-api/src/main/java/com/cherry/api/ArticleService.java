package com.cherry.api;


import com.cherry.domain.blog.entity.Article;

import java.util.List;

public interface ArticleService {

    List<Article> list();

    void add(Article article);

    void delete(Long id);

    void update(Article article);

    Article get(Long id);
}
