package com.yq.blog.controller;

import com.yq.blog.common.aop.LogAnnotation;
import com.yq.blog.common.cache.Cache;
import com.yq.blog.service.ArticleService;
import com.yq.blog.vo.params.PageParams;
import com.yq.blog.vo.params.PublishParams;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json
@RestController
@RequestMapping("articles")
public class ArticleController {
    /**
     * 文章列表
     *
     */
    @Autowired
    private ArticleService articleService;

    @Cache(expire = 5*60*1000,name = "listArticles")
    //对此接口通过AOP记录日志
    @LogAnnotation(module="文章",operator="获取文章列表")
    @PostMapping
    public Result listArticles(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }

    /**
     * 首页最热文章
     * @return
     */
    @Cache(expire = 5*60*1000,name = "hotArticles")
    @PostMapping("hot")
    public Result hotArticles(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }
    /**
     * 首页最新文章
     * @return
     */
    @Cache(expire = 5*60*1000,name = "newArticles")
    @PostMapping("new")
    public Result newArticles(){
        int limit = 5;
        return articleService.newArticle(limit);
    }

    /**
     * 文章归档
     * @return
     */
    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();

    }

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @Cache(expire = 5*60*1000,name = "articleDetails")
    @PostMapping("view/{id}")
    public Result articleDetails(@PathVariable("id") Long articleId){
        return articleService.findArticleDetailsById(articleId);
    }

    @PostMapping("publish")
    public Result publish(@RequestBody PublishParams publishParams){
        return articleService.publishArticle(publishParams);
    }

    @PostMapping("{id}")
    public Result getArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

}
