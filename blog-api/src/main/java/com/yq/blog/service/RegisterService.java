package com.yq.blog.service;

import com.yq.blog.vo.params.RegisterParams;
import com.yq.blog.vo.params.Result;


public interface RegisterService {

    /**
     * 注册
     * @param registerParams
     * @return
     */
    Result register(RegisterParams registerParams);
}
