package com.yq.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yq.blog.dao.mapper.UserMapper;
import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.service.SysUserService;
import com.yq.blog.utils.JWTUtils;
import com.yq.blog.vo.ErrorCode;
import com.yq.blog.vo.LoginVo;
import com.yq.blog.vo.UserVo;
import com.yq.blog.vo.params.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public SysUser selectAuthorByAuthorId(long AuthorId) {
        SysUser sysUser= userMapper.selectById(AuthorId);
        if(sysUser==null){
            sysUser=new SysUser();

        }
        return  sysUser;
    }

    @Override
    public SysUser selectUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");

        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 获取当前用户信息
     * @param token
     * @return
     */
    @Override
    public Result getCurrentUser(String token) {
        /**
         * 1.验证token是否为空
         * 2.验证token是否解析成功
         * 3.验证redis中是否存在
         */
        //token为空
        if(StringUtils.isBlank(token)){
            return Result.fail(ErrorCode.TOKEN_BLANK.getCode(), ErrorCode.TOKEN_BLANK.getMsg());
        }
        //token解析失败
        Map<String,Object>map = JWTUtils.checkToken(token);
        if(map==null){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
        }
        //token过期 不在redis中
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank((userJson))){
            return Result.fail(ErrorCode.LOGIN_EXPIRATION.getCode(), ErrorCode.LOGIN_EXPIRATION.getMsg());
        }
        SysUser sysUser = JSON.parseObject(userJson,SysUser.class);
        LoginVo loginVo = new LoginVo();
        loginVo.setAccount(sysUser.getAccount());
        loginVo.setAvatar(sysUser.getAvatar());
        loginVo.setId(sysUser.getId());
        loginVo.setNickname(sysUser.getNickname());

        return Result.success(loginVo);
    }

    @Override
    public SysUser selectUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");

        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addUser(SysUser sysUser) {
        //添加成功
        if(userMapper.insert(sysUser)!=0){
            return true;
        }
        else
            return false;
    }

    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        //token解析失败
        Map<String,Object>map = JWTUtils.checkToken(token);
        if(map==null){
            return null;
        }
        //token过期 不在redis中
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank((userJson))){
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson,SysUser.class);
        return sysUser;
    }

    @Override
    public UserVo selectUserVoByAuthorId(long authorId) {
        SysUser sysUser= userMapper.selectById(authorId);
        UserVo userVo = new UserVo();
        if(sysUser==null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("风中一匹狼");
        }
        userVo.setAvatar(sysUser.getAvatar());
        userVo.setId(sysUser.getId());
        userVo.setNickname(sysUser.getNickname());
        return  userVo;
    }
}
