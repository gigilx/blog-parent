package com.yq.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yq.blog.common.aop.LogAnnotation;
import com.yq.blog.dao.mapper.ArticleMapper;
import com.yq.blog.dao.pojo.Article;
import com.yq.blog.utils.RedisIncrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@EnableAsync
public class ThreadService {
    @Autowired
    ArticleMapper articleMapper;
    @Async("taskExecutor")
    @Scheduled(cron = "0 0/5 * * * ?")
    @LogAnnotation(module = "定时同步任务",operator = "文章阅读量")
    public void updateArticleViewCounts(){
        String prefix = "article::view_counts::";
        List<String> keys = RedisIncrUtils.getAllKeysBeginWithPattern(prefix);
        if(keys!=null){
            for (String key : keys) {
                String articleId = key.substring(prefix.length());
                Article updateArticle = new Article();
                updateArticle.setId(Long.valueOf(articleId));
                updateArticle.setViewCounts(RedisIncrUtils.getNum(key));
                articleMapper.updateById(updateArticle);
            }
        }



//        LambdaQueryWrapper<Article>queryWrapper = new LambdaQueryWrapper<>();
//        //此处new一个是因为mapper中update只会更新有值的字段，提高性能 ，且int会默认为0，故Atricle中设置为integer
//        Article updateArticle = new Article();
//        updateArticle.setViewCounts(article.getViewCounts()+1);
//        queryWrapper.eq(Article::getId,article.getId());
//        //先判断阅读量是否改变，没改变再更新
//        queryWrapper.eq(Article::getViewCounts,article.getViewCounts());
//        //update article set view_counts = ? where article_id = ? and view_counts = ?
//        articleMapper.update(updateArticle, queryWrapper);


//        try {
//            Thread.sleep(5000);
//            System.out.println("更新完成");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
    @Async("taskExecutor")
    @Scheduled(cron = "0 0/5 * * * ?")
    @LogAnnotation(module = "定时同步任务",operator = "文章评论数")
    public void updateArticleCommentCounts(){

        String prefix = "article::comment_counts::";
        List<String> keys = RedisIncrUtils.getAllKeysBeginWithPattern(prefix);
        if(keys!=null){
            for (String key : keys) {
                String articleId = key.substring(prefix.length());
                Article updateArticle = new Article();
                updateArticle.setId(Long.valueOf(articleId));
                updateArticle.setCommentCounts(RedisIncrUtils.getNum(key));
                articleMapper.updateById(updateArticle);
            }
        }
//        LambdaQueryWrapper<Article>queryWrapper = new LambdaQueryWrapper<>();
//        //此处new一个是因为mapper中update只会更新有值的字段，提高性能 ，且int会默认为0，故Atricle中设置为integer
//        Article updateArticle = new Article();
//        updateArticle.setCommentCounts(article.getCommentCounts()+1);
//        queryWrapper.eq(Article::getId,article.getId());
//        //先判断阅读量是否改变，没改变再更新
//        queryWrapper.eq(Article::getCommentCounts,article.getCommentCounts());
//        articleMapper.update(updateArticle, queryWrapper);
    }
}
