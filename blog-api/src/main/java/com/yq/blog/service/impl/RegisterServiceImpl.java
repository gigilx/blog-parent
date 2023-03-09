package com.yq.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yq.blog.dao.mapper.UserMapper;
import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.service.RegisterService;
import com.yq.blog.service.SysUserService;
import com.yq.blog.utils.JWTUtils;
import com.yq.blog.vo.ErrorCode;
import com.yq.blog.vo.params.RegisterParams;
import com.yq.blog.vo.params.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Ref;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    private String salt = "#aa@!!";

    /**
     * 注册
     * @param registerParams
     * @return
     */
    @Override
    public Result register(RegisterParams registerParams) {
        /**
         * 1.参数是否合法
         * 2.判断用户名是否已存在
         * 2.插入数据库并生成token
         * 3.token放入redis
         */
        String account = registerParams.getAccount();
        String password = registerParams.getPassword();
        String nickname = registerParams.getNickname();
        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)||StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        password = DigestUtils.md5Hex(password+salt); //加密
        SysUser sysUser = sysUserService.selectUser(account, password);
        if(sysUser!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser = new SysUser();
        sysUser.setAccount(account);
        sysUser.setPassword(password);
        sysUser.setNickname(nickname);
        if(!sysUserService.addUser(sysUser)){
            return Result.fail(ErrorCode.OPERATION_FAIL.getCode(), ErrorCode.OPERATION_FAIL.getMsg());
        }
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser));

        return Result.success(token);
    }
}
