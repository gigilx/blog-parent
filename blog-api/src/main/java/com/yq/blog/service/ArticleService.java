package com.yq.blog.service;

import com.yq.blog.vo.params.PageParams;
import com.yq.blog.vo.params.PublishParams;
import com.yq.blog.vo.params.Result;
import org.springframework.stereotype.Service;
import java.util.List;

public interface ArticleService {
    /**
     *文章列表
     * @param pageParams
     * @return
     */
    Result  listArticle(PageParams pageParams);

    /**
     * 最热
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 最新
     * @param limit
     * @return
     */
    Result newArticle(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    Result findArticleDetailsById(Long articleId);


    /**
     * 发布文章
     * @param publishParams
     * @return
     */
    Result publishArticle(PublishParams publishParams);

    /**
     * 编辑文章
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);
}
