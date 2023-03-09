package com.yq.blog.vo.params;

import com.yq.blog.dao.pojo.ArticleBody;
import com.yq.blog.dao.pojo.Category;
import com.yq.blog.dao.pojo.Tag;
import com.yq.blog.vo.CategoryVo;
import lombok.Data;

import java.util.List;

@Data
public class PublishParams {
    private  Long id;

    private ArticleBody body;

    private CategoryVo category;

    private String summary;

    private List<Tag> tags;

    private String title;

}
