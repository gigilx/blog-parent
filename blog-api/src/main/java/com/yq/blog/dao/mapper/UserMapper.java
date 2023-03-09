package com.yq.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yq.blog.dao.pojo.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<SysUser> {
}
