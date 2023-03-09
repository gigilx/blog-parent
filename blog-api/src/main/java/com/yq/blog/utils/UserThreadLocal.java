package com.yq.blog.utils;

import com.yq.blog.dao.pojo.SysUser;

public class UserThreadLocal {
    private UserThreadLocal(){};

    private final static ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static void put(SysUser sysUser){
        LOCAL.set(sysUser);

    }

    public static SysUser get(){
       return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
