package com.yq.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yq.blog.dao.dos.Archives;
import com.yq.blog.dao.pojo.Article;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Repository
public interface ArticleMapper extends BaseMapper <Article> {

    List<Archives> listArchives();

    IPage<Article> listArticles(Page<Article> page,
                               Long categoryId,
                               Long tagId,
                               String year,
                               String month
                               );
}
