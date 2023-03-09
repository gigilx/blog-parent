package com.yq.blog.common.cache;

import java.lang.annotation.*;

//Type代表可以放在类上 method代表可以放在方法上
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    long expire() default 5*60*1000 ;
    String name() default "";
}
