package com.yq.blog.service;

import com.alibaba.fastjson.JSON;
import com.yq.blog.service.ArticleService;
import com.yq.blog.vo.ArticleMessage;
import com.yq.blog.vo.params.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Component
@RocketMQMessageListener(topic = "blog-update-article",consumerGroup = "blog-update-article-group")
public class ArticleListener implements RocketMQListener<ArticleMessage> {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(ArticleMessage message) {
        log.info("收到的消息:{}",message);
        //做什么了，更新缓存
        //1. 更新查看文章详情的缓存
        Long articleId = message.getArticleId();
        String params = DigestUtils.md5Hex(articleId.toString());
        String redisKey = "articleDetails::com.yq.blog.controller.ArticleController::articleDetails::"+params;
        Result articleResult = articleService.findArticleDetailsById(articleId);
        redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(articleResult), Duration.ofMillis(5 * 60 * 1000));
        log.info("更新了文章详情缓存:{}",redisKey);
        //最新文章
        int limit = 5;
//        String newArticleParams = DigestUtils.md5Hex(String.valueOf(limit));
        String newArticlesRedisKey = "newArticles::com.yq.blog.controller.ArticleController::newArticles::";
        Result newArticles = articleService.newArticle(limit);
        redisTemplate.opsForValue().set(newArticlesRedisKey,JSON.toJSONString(newArticles), Duration.ofMillis(5 * 60 * 1000));
        log.info("更新了最新文章缓存:{}", newArticlesRedisKey);
        //2. 文章列表的缓存 不知道参数,解决办法 直接删除缓存
        Set<String> keys = redisTemplate.keys("listArticle*");
        keys.forEach(s -> {
            redisTemplate.delete(s);
            log.info("删除了文章列表的缓存:{}",s);
        });

    }
}
