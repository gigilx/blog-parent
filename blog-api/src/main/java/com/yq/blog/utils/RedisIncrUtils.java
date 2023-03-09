package com.yq.blog.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RedisIncrUtils {


    private static RedisTemplate<String,Integer> redisTemplate;

    @Autowired
    public  void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisIncrUtils.redisTemplate = redisTemplate;
    }

    public static boolean isExists(String key){
        return redisTemplate.hasKey(key);
    }
    public static void setKey(String key,Integer num){
        redisTemplate.opsForValue().set(key,num);
    }
    public static void incr(String key){
        redisTemplate.opsForValue().set(key,getNum(key)+1);
    }
    public static List<String> getAllKeysBeginWithPattern(String pattern){

        pattern = pattern + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if(keys!=null){
            return new ArrayList<>(keys);
        }else{
            return null;
        }

    }
    public static List<String> getAllKeysBeginArticleComments(){
        String pattern = "article::comments::*";
        Set<String> keys = redisTemplate.keys(pattern);
        return new ArrayList<>(keys);
    }
    public static int getNum(String key){
        return redisTemplate.opsForValue().get(key);
    }

}
