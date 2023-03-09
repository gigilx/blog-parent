package com.yq.blog.handler;

import com.alibaba.fastjson.JSON;
import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.service.SysUserService;
import com.yq.blog.utils.JWTUtils;
import com.yq.blog.utils.UserThreadLocal;
import com.yq.blog.vo.ErrorCode;
import com.yq.blog.vo.params.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserService sysUserService;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{
        /**
         * 1.验证接口路径是否为controller的方法 可能访问静态资源
         * 2.验证token是否为空
         * 3.验证token是否合法
         */
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        String token = request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if(StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return  false;
        }
        SysUser sysUser = sysUserService.checkToken(token);
        if(sysUser==null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //登录状态放行
        UserThreadLocal.put(sysUser);
        return  true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();   //防止内存泄露
    }
}
