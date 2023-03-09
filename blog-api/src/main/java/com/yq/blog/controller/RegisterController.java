package com.yq.blog.controller;

import com.yq.blog.service.RegisterService;
import com.yq.blog.vo.params.RegisterParams;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;
    @PostMapping
    public Result register(@RequestBody RegisterParams registerParams){
        return registerService.register(registerParams);

    }
}
