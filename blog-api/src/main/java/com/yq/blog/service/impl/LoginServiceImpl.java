package com.yq.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.qiniu.util.Json;
import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.service.LoginService;
import com.yq.blog.service.SysUserService;
import com.yq.blog.utils.JWTUtils;
import com.yq.blog.vo.ErrorCode;
import com.yq.blog.vo.params.LoginParams;
import com.yq.blog.vo.params.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    private String salt = "#aa@!!";
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public Result login(LoginParams loginParams) {
        /**
         * 1.参数是否合法
         * 2.如果合法则查询账户密码是否正确，并返回token
         * 3.如果不合法则返回错误
         * 4
         */
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        //参数为空
        if(StringUtils.isBlank(account)|| StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        password = DigestUtils.md5Hex(password+salt); //h5加密
        SysUser sysUser = sysUserService.selectUser(account,password);
        if (sysUser==null)
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());

        String token = JWTUtils.createToken(sysUser.getId());
        //放入redis
//        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    /**
     * 登出
     * @return
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_" + token);
        return Result.success(null);
    }
}
