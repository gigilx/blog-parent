package com.yq.blog.service;

import com.yq.blog.dao.pojo.SysUser;
import com.yq.blog.vo.UserVo;
import com.yq.blog.vo.params.Result;

public interface SysUserService {
    /**
     * 根据id查询用户
     * @param AuthorId
     * @return
     */
    SysUser selectAuthorByAuthorId(long AuthorId);

    /**
     * 根据账户名密码查询//登录
     * @param account
     * @param password
     * @return
     */
    SysUser selectUser(String account,String password);

    /**
     * 根据token获取当前用户信息
     * @param token
     * @return
     */
    Result getCurrentUser(String token);

    /**
     * 查询用户名
     * @param account
     * @return
     */
    SysUser selectUserByAccount(String account);

    /**
     * 添加用户
     * @param sysUser
     * @return
     */
    boolean addUser(SysUser sysUser);

    /**
     * 检验token
     * @param token
     * @return
     */
    SysUser checkToken(String token);

    /**
     * 评论用户信息
     * @param authorId
     * @return
     */
    UserVo selectUserVoByAuthorId(long authorId);


}
