package com.yq.blog.utils;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {
    private static final String jwtToken="123456yxx!@@$$";

    //创建密钥
    public static String createToken(Long userId){
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", userId);
//        claims.put("userName", "xxx");
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,jwtToken) //签发算法
                .setClaims(claims) //body数据 唯一
                .setIssuedAt(new Date()) //签发时间
                .setExpiration(new Date(System.currentTimeMillis()+24*60*60*1000));//一天有效期
        String token = jwtBuilder.compact();
        return token;
    }
    //检查密钥
    public  static Map<String ,Object> checkToken(String token){
        try{
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return (Map<String, Object>) parse.getBody(); //body 用户信息 json格式 转为MAp
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        String token = JWTUtils.createToken(100L);
//        System.out.println(token);
//        Map<String,Object> map = JWTUtils.checkToken(token);
//        System.out.println(map);
//    }
}
