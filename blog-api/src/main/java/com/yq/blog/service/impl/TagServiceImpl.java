package com.yq.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yq.blog.dao.mapper.TagMapper;
import com.yq.blog.dao.pojo.Tag;
import com.yq.blog.service.TagService;
import com.yq.blog.vo.TagVo;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Override
    public List<TagVo> findTagsByArticleId(long articleId) {
        List<Tag> tags= tagMapper.selectTagsByArticleId(articleId);
        return copyList(tags);
    }

    @Override
    public Result hotTags(int limit) {
        /**
         * 1.标签对应文章数量最多即为最热标签
         * 2.group by tag_id 再倒序排列
         */

        return Result.success(copyList(tagMapper.selectHotTags(limit)));
    }

    @Override
    public Result getAllTags() {
        return Result.success(copyList(tagMapper.selectList(new LambdaQueryWrapper<>())));

    }

    @Override
    public Result getAllTagsDetails() {
        return Result.success(tagMapper.selectList(new LambdaQueryWrapper<>()));

    }

    @Override
    public Result getTagDetailsByTagId(long TagId) {
        return Result.success(tagMapper.selectById(TagId));
    }

    private List<TagVo> copyList(List<Tag> tags){
        List<TagVo> tagVos = new ArrayList<>();
        for(Tag tag : tags){
            tagVos.add(copy(tag));
        }
        return tagVos;
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag, tagVo);
        return tagVo;

    }
}
