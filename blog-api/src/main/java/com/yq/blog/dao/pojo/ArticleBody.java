package com.yq.blog.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ArticleBody {
    @TableId(type= IdType.AUTO)
    private Long id;
    private String content;
    private String contentHtml;
    private Long articleId;
}