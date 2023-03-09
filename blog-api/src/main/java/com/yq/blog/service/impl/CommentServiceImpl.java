package com.yq.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yq.blog.dao.mapper.ArticleMapper;
import com.yq.blog.dao.mapper.CommentMapper;
import com.yq.blog.dao.pojo.Article;
import com.yq.blog.dao.pojo.Comment;
import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.service.ArticleService;
import com.yq.blog.service.CommentService;
import com.yq.blog.service.SysUserService;
import com.yq.blog.service.ThreadService;
import com.yq.blog.utils.RedisIncrUtils;
import com.yq.blog.utils.UserThreadLocal;
import com.yq.blog.vo.CommentVo;
import com.yq.blog.vo.params.CommentParams;
import com.yq.blog.vo.params.Result;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public Result getCommentsByArticleId(long articleId) {
        /**
         * 1.查询到评论
         * 2.根据评论的authorId查询user信息
         * 3.根据parentId找到父评论
         * 4.根据toId找到父用户信息
         */

        //方法1.一次查完进行封装
//        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Comment::getArticleId,articleId);
//        List<Comment> comments = commentMapper.selectList(queryWrapper);
//        List<CommentVo> commentVos = new ArrayList<>();
//        for (Comment comment : comments){
//
//            commentVos.add(copy(comment));
//
//
//        }
//        for (Comment comment : comments){
//            int level = comment.getLevel();
//            CommentVo commentVo1 = copy(comment);
//            if(level==2){
//                long parent = comment.getParentId();
//                Iterator<CommentVo> iterator = commentVos.iterator();
//                for(CommentVo commentVo:commentVos){
//                    if(commentVo.getId()==parent){
//                        if(commentVo.getChildrens()!=null)
//                            commentVo.getChildrens().add(commentVo1);
//                        else{
//                            List<CommentVo>commentVoList = new ArrayList<CommentVo>();
//                            commentVoList.add(commentVo1);
//                            commentVo.setChildrens(commentVoList);
//                        }
//
//
//                            commentVos.remove(commentVo1);
//                            break;
//
//                    }
//                }
//
//            }
//        }
//
//        return Result.success(commentVos);

        //方法2.循环查表
        /**
         * 先查父评论，再根据父评论id和parentId找子评论
         */
        LambdaQueryWrapper<Comment>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,articleId);
        queryWrapper.eq(Comment::getLevel,1);
        return Result.success(copyList(commentMapper.selectList(queryWrapper)));

    }


    @Override
    public List<Comment> findCommentsByParentId(long parentId) {
        LambdaQueryWrapper<Comment>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,parentId);
        queryWrapper.eq(Comment::getLevel,2);

        return commentMapper.selectList(queryWrapper);
    }
    @Autowired
    private ThreadService threadService;
    @Override
    public Result writeComment(CommentParams commentParams) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParams.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParams.getContent());
        comment.setParentId(commentParams.getParent());
        comment.setToUid(commentParams.getToUserId());
        if(commentParams.getParent()==null){
            comment.setLevel(1);
            comment.setParentId(new Long(0));
            comment.setToUid(new Long(0));
        }
        else if(commentParams.getParent()>0)
            comment.setLevel(2);
        comment.setCreateDate(System.currentTimeMillis());

        commentMapper.insert(comment);
        //评论数+1
        Article article = articleMapper.selectById(commentParams.getArticleId());
        String key = "article" + "::" + "comment_counts" + "::" + article.getId();
        if(!RedisIncrUtils.isExists(key)){
            RedisIncrUtils.setKey(key,article.getCommentCounts()+1);
        }else{
            RedisIncrUtils.incr(key);
        }

        return Result.success(null);
    }


    //通过父评论找子评论
    private List<CommentVo> copyList(List<Comment> comments){
        List<CommentVo>commentVoList = new ArrayList<>();
        for(Comment comment:comments){
            CommentVo commentVo = copy(comment);
            //找子评论
            if(comment.getLevel()==1){
                commentVo.setChildrens(copyList(findCommentsByParentId(comment.getId())));
            }
            commentVoList.add(commentVo);
        }
        return  commentVoList;
    }

    private CommentVo copy(Comment comment){
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        commentVo.setCreateDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        commentVo.setAuthor(sysUserService.selectUserVoByAuthorId(comment.getAuthorId()));
        if(commentVo.getLevel()>1){
            commentVo.setToUser(sysUserService.selectUserVoByAuthorId(comment.getToUid()));
        }

        return commentVo;
    }
}
