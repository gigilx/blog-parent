package com.yq.blog.controller;

import com.yq.blog.service.LoginService;
import com.yq.blog.vo.params.LoginParams;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginController {

    /**
     * 登录
     */
    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParams loginParams){
        return loginService.login(loginParams);
    }


}
