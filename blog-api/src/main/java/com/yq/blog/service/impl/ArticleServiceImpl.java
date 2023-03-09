package com.yq.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yq.blog.dao.mapper.ArticleBodyMapper;
import com.yq.blog.dao.mapper.ArticleMapper;
import com.yq.blog.dao.mapper.ArticleTagMapper;
import com.yq.blog.dao.pojo.*;
import com.yq.blog.service.*;
import com.yq.blog.utils.RedisIncrUtils;
import com.yq.blog.utils.UserThreadLocal;
import com.yq.blog.vo.ArticleBodyVo;
import com.yq.blog.vo.ArticleMessage;
import com.yq.blog.vo.ArticleVo;
import com.yq.blog.vo.params.PageParams;
import com.yq.blog.vo.params.PublishParams;
import com.yq.blog.vo.params.Result;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private CategoryService categoryService;


    @Override
    public Result listArticle(PageParams pageParams){
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticles(page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true,false,false));
    }

    //查询年月不能直接用mybatisplus
//    @Override
//    public Result listArticle(PageParams pageParams) {
//        /**
//         * 1.分页查询article
//         */
//        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
//        LambdaQueryWrapper<Article>queryWrapper=new LambdaQueryWrapper<>();
//
//        if(pageParams.getCategoryId() !=null){
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//
//        }
//        List<Long>articleIdList = new ArrayList<>();
//        if(pageParams.getTagId()!=null){
//            LambdaQueryWrapper<ArticleTag>queryWrapper1 = new LambdaQueryWrapper<>();
//            queryWrapper1.eq(ArticleTag::getTagId,pageParams.getTagId());
//            queryWrapper1.select(ArticleTag::getArticleId);
//            List<ArticleTag>articleTagList = articleTagMapper.selectList(queryWrapper1);
//            for(ArticleTag articleTag:articleTagList){
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if(articleIdList.size()>0)
//                queryWrapper.in(Article::getId,articleIdList);
//            //select * form article where article_id in articleIdList
//        }
//
//
//
//        //置顶排序 order by create_date desc
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper); //传入查询条件
//        List<Article> records  =articlePage.getRecords();
//        List<ArticleVo> articleVos = copyList(records,true,true,false,false);
//        return Result.success(articleVos);
//
//    }

    /**
     * 最热
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        // select id,title from ms_article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper); // 传入查询语句

        return Result.success(copyList(articles,false,false,false,false));
    }

    /**
     * 最新
     * @param limit
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        // select id,title from ms_article order by create_date desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper); // 传入查询语句
        return Result.success(copyList(articles,false,false,false,false));
    }

    /**
     * 文章归档
     * @return
     */
    @Override
    public Result listArchives() {

        return Result.success(articleMapper.listArchives());
    }

    @Autowired
    private ThreadService threadService;
    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleDetailsById(Long articleId) {
        /**
         * 阅读量更新
         * 1.更新带写锁，此时其他操作无法进行，性能降低
         * 2.如果更新出问题，则其他操作也会卡住，不能查看文章
         * 3.如果放入线程池，则不会影响其他线程
         */
        Article article = articleMapper.selectById(articleId);
        //阅读数+1
        String key = "article" + "::" + "view_counts" + "::" + articleId;
        if(!RedisIncrUtils.isExists(key)){
            RedisIncrUtils.setKey(key,article.getViewCounts()+1);
        }else{
            RedisIncrUtils.incr(key);
        }
//        threadService.updateArticleViewCount(String key);
//        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(copy(article, true, true, true, true));
    }

    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Override
    public Result publishArticle(PublishParams publishParams) {
        /**
         * //判断是否已经存在，若存在则为编辑
         * 1.获取用户id （登录拦截）
         * 2.插入article 获取articleId
         * 3.插入tags
         * 4.插入articleBody
         */
        boolean isEdit = false;
        Article article = new Article();
        Map<String, String> map = new HashMap<>();
        ArticleBody articleBody = new ArticleBody();
        if(publishParams.getId()!=null){
            isEdit = true;
        }
        if(!isEdit) {
            SysUser sysUser = UserThreadLocal.get();
            article.setAuthorId(sysUser.getId());
            article.setSummary(publishParams.getSummary());
            article.setCategoryId(publishParams.getCategory().getId());
            article.setCreateDate(System.currentTimeMillis());
            article.setWeight(Article.Article_Common);
            article.setViewCounts(0);
            article.setCommentCounts(0);
            article.setTitle(publishParams.getTitle());
            articleMapper.insert(article);
            long articleId = article.getId();

            List<Tag> tags = publishParams.getTags();
            for (Tag tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }

            articleBody.setArticleId(articleId);
            articleBody.setContent(publishParams.getBody().getContent());
            articleBody.setContentHtml(publishParams.getBody().getContentHtml());
            articleBodyMapper.insert(articleBody);

            article.setBodyId(articleBody.getId());
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(Article::getId, articleId);
            articleMapper.update(article, queryWrapper);


        }else{
            article.setId(publishParams.getId());
            article.setSummary(publishParams.getSummary());
            article.setCategoryId(publishParams.getCategory().getId());
            article.setTitle(publishParams.getTitle());
            articleMapper.updateById(article);

            List<Tag> tags = publishParams.getTags();
            LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ArticleTag::getArticleId,article.getId());
            articleTagMapper.delete(queryWrapper);
            for (Tag tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(article.getId());
                articleTagMapper.insert(articleTag);
            }

            LambdaQueryWrapper<ArticleBody> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(ArticleBody::getArticleId,article.getId());
            articleBody.setContent(publishParams.getBody().getContent());
            articleBody.setContentHtml(publishParams.getBody().getContentHtml());
            articleBodyMapper.update(articleBody, queryWrapper1);

        }
        map.put("id", article.getId().toString());
        //放入消息队列
        ArticleMessage articleMessage = new ArticleMessage();
        articleMessage.setArticleId(article.getId());
        rocketMQTemplate.convertAndSend("blog-update-article",articleMessage);

        return Result.success(map);
    }

    @Override
    public Result findArticleById(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        return Result.success(copy(article, true, false, true, true));
    }


    //复制articles集合到articlesVo
    private List<ArticleVo> copyList(List<Article> records,boolean isTag , boolean isAuthor,boolean isArticleBody,boolean isCategory){
        List<ArticleVo> articleVos = new ArrayList<>();
        for(Article record : records){
            articleVos.add(copy(record,isTag ,isAuthor, isArticleBody, isCategory));

        }
        return  articleVos;
    }
//复制单个对象
    private ArticleVo copy(Article article,boolean isTag , boolean isAuthor,boolean isArticleBody,boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        //属性名相同自动复制
        BeanUtils.copyProperties(article, articleVo);
        //时间是long  转string
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if(isTag){
            long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.selectUserVoByAuthorId(authorId));

        }
        if(isArticleBody){
            long articleId = article.getId();

            articleVo.setBody(findArticleBodyById(articleId));
        }
        if(isCategory){
            long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));

        }
        return articleVo;

    }

    public ArticleBodyVo findArticleBodyById(Long articleId){
        LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleBody::getArticleId,articleId);
//            queryWrapper.select(ArticleBody::getContent);
        queryWrapper.last("limit 1");
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBodyMapper.selectOne(queryWrapper).getContent());
        return articleBodyVo;
    }
}

