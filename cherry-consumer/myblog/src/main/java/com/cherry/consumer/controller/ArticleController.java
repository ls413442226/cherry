package com.cherry.consumer.controller;

import com.cherry.consumer.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value="V1")
@RestController
@RequestMapping("/api")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    //获取文章
    @ApiOperation(value="获取文章")
    @GetMapping("/articles")
    public Map getArticles(){

        return null;
    }
}
