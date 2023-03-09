package com.yq.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yq.blog.dao.pojo.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> selectTagsByArticleId(long articleId);

    List<Tag> selectHotTags(int limit);
}
