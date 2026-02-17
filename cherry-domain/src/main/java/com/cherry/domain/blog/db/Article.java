package com.cherry.domain.blog.db;

import java.io.Serializable;
import java.time.LocalDateTime;

//@Data
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    //文章id
    private Long id;
    //文章名
    private String title;
    //文章内容
    private String content;
    //文章创建时间
    private LocalDateTime createdAt;

    // 无参构造方法
    public Article(){

    }
    //有参构造方法
    public Article(Long id,String title,String content,LocalDateTime createdAt){
        this.id=id;
        this.title=title;
        this.content=content;
        this.createdAt=createdAt;
    }

    //get/set方法
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content=content;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt=createdAt;
    }

    //toString方法(便于调试和输出对象信息)

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
