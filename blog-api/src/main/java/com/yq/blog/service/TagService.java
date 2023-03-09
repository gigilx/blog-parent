package com.yq.blog.service;

import com.yq.blog.dao.pojo.Tag;
import com.yq.blog.vo.TagVo;
import com.yq.blog.vo.params.Result;

import java.util.List;

public interface TagService {

    /**
     * 标签
     * @param id
     * @return
     */
    List <TagVo> findTagsByArticleId(long id);

    /**
     * 最热标签
     * @param limit
     * @return
     */
    Result hotTags(int limit);

    /**
     * 获取所有标签
     * @return
     */
    Result getAllTags();

    /**
     * 标签细节
     * @return
     */
    Result getAllTagsDetails();

    /**
     * 单个标签
     * @param TagId
     * @return
     */
    Result getTagDetailsByTagId(long TagId);
}
