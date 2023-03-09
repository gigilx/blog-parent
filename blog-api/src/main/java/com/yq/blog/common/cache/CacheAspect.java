package com.yq.blog.common.cache;

import com.alibaba.fastjson.JSON;
import com.qiniu.util.Json;
import com.yq.blog.vo.params.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Component
@Aspect //切面 定义了通知和切点的关系
@Slf4j
public class CacheAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.yq.blog.common.cache.Cache)")
    public void pt() {
    }

    @Around("pt()")
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            /**
             * 1.通过切入点拿到类名、方法名、参数
             * 2.参数不为空则去redis中查，key为类名+方法名+参数
             * 3.redis中若无则放入redis中
             */


            Signature signature = joinPoint.getSignature();
//            MethodSignature methodSignature = (MethodSignature) signature;
//            Method signatureMethod = methodSignature.getMethod();

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();

            Class[] parameterType = new Class[joinPoint.getArgs().length]; //放参数
            Object[] args = joinPoint.getArgs();  //参数

            String params = "";
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    parameterType[i] = args[i].getClass();
                    params += JSON.toJSONString(args[i]);
                } else {
                    parameterType[i] = null;
                }
            }

            if (!StringUtils.isBlank(params)) {
                params = DigestUtils.md5Hex(params);
                //以防参数过长以及字符转义获取不到
            }

            //拿到cache注解
            Method method = signature.getDeclaringType().getMethod(methodName, parameterType);
            Cache annotation = method.getAnnotation(Cache.class);
            long expire = annotation.expire(); //缓存过期时间
            String name = annotation.name();  //缓存名
            //判断redis中是否存在
            String redisKey = name + "::" + className + "::" + methodName + "::" + params;
            String redisValue = redisTemplate.opsForValue().get(redisKey);

            //before
            if (!StringUtils.isBlank(redisValue)) {
                log.info("走了缓存~~~,{},{}", className, methodName);
                log.info("key,{}",redisKey);
                return JSON.parseObject(redisValue, Result.class);
            }
            //执行切点的原有方法
            Object result = joinPoint.proceed();
            //after
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(result), Duration.ofMillis(expire));
            log.info("存入缓存~~~ {},{}", className, methodName);
            log.info("key,{}",redisKey);
            return result;


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Result.fail(-999, "系统异常");
    }
}
