package com.yq.blog.service;

import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.vo.params.LoginParams;
import com.yq.blog.vo.params.Result;

public interface LoginService {
    Result login(LoginParams loginParams);

    Result logout(String token);



}
