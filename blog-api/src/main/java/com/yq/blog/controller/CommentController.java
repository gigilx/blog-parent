package com.yq.blog.controller;

import com.yq.blog.service.CommentService;
import com.yq.blog.vo.params.CommentParams;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @GetMapping("article/{id}")
    public Result getComments(@PathVariable("id") long articleId){
        return commentService.getCommentsByArticleId(articleId);
    }
    @PostMapping("create/change")
    public Result wirteComment(@RequestBody CommentParams commentParams){
        return  commentService.writeComment(commentParams);
    }
}
