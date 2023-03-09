package com.yq.blog.controller;

import com.yq.blog.service.LoginService;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logout")
public class LogoutController {

    @Autowired
    private LoginService loginService;

    /**
     * 登出
     * @return
     */
    @GetMapping
    public Result logout(@RequestHeader("Authorization") String token){
        return  loginService.logout(token);
    }
}
