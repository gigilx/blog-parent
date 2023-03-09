package com.yq.blog.service;

import com.yq.blog.dao.pojo.Comment;
import com.yq.blog.vo.params.CommentParams;
import com.yq.blog.vo.params.Result;

import java.util.List;

public interface CommentService {
    /**
     * 获取文章评论
     * @param articleId
     * @return
     */
    Result getCommentsByArticleId(long articleId);

    /**
     * 找子评论
     * @param parentId
     * @return
     */
    List<Comment> findCommentsByParentId(long parentId);

    /**
     * 写评论
     * @param commentParams
     * @return
     */
    Result writeComment(CommentParams commentParams);
}
