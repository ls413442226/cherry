package com.cherry.controller;

import com.cherry.service.ArticleService;
import com.cherry.domain.blog.db.Article;
import com.cherry.domain.vo.Result;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.Resource;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/blog/articles")
@OpenAPIDefinition(
        info = @Info(title = "My API", version = "1.0", description = "blogAPI")
)
public class ArticleController {

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private ArticleService articleService;

    //获取文章
    @Operation(summary = "获取文章列表", description = "获取文章列表")
    @GetMapping("/list")
    public Result<List<Article>> list(@RequestHeader("userId")Long userId){
        System.out.println("当前用户ID:"+userId);
        return Result.success(articleService.list());
    }

    @Operation(summary = "获取文章", description = "获取文章")
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable("id") Long id){
        return Result.success(articleService.get(id));
    }

    @Operation(summary = "添加文章", description = "添加文章")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Article article){
        articleService.add(article);
        return Result.<Void>success(null);
    }
    @Operation(summary = "删除文章", description = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id){
        articleService.delete(id);
        return Result.<Void>success(null);
    }

    @Operation(summary = "更新文章", description = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Long id,@RequestBody Article article){
        article.setId(id);
        articleService.update(article);
        return Result.<Void>success(null);
    }
}
