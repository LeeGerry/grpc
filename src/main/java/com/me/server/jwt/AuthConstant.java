package com.me.server.jwt;

import io.grpc.Context;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public interface AuthConstant {
    // JWT秘钥
    SecretKey JWT_KEY = Keys.hmacShaKeyFor("hello_grpc_666_adfdfaif_addd333fffsdfasdf".getBytes());
    // 客户端ID，即客户端发送来的请求携带了JWT字符串，通过JWT字符串确认了用户身份，就存在这个变量中
    Context.Key<String> AUTH_CLIENT_ID = Context.key("clientId");
    // 携带JWT字符串的请求头的KEY
    String AUTH_HEADER = "Authorization";
    // 携带JWT字符串的请求头的参数前缀，通过它可以确认参数的类型，常见取值有Bearer和Basic
    String AUTH_TOKEN_TYPE = "Bearer";
}
