package com.yq.blog.handler;

import com.yq.blog.vo.params.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//对添加@controller的方法进行拦截 AOP
@ControllerAdvice

public class AllExpectionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody //返回json而不是页面
    public Result doExpection(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999, "系统异常");
    }
}
