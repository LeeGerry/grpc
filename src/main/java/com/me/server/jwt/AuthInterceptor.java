package com.me.server.jwt;

import io.grpc.*;
import io.jsonwebtoken.*;

public class AuthInterceptor implements ServerInterceptor {
    private JwtParser parser = Jwts.parserBuilder().setSigningKey(AuthConstant.JWT_KEY).build();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        // 1. 从Metadata中提取出当前请求所携带的JWT字符串，相当于从请求头中提取出来
        String authorization = metadata.get(Metadata.Key.of(AuthConstant.AUTH_HEADER, Metadata.ASCII_STRING_MARSHALLER));
        Status status = Status.OK;
        if (authorization == null) {
            // 2. 如果第一步提取到的值是null
            status = Status.UNAUTHENTICATED.withDescription("miss authentication token");
        } else if (!authorization.startsWith(AuthConstant.AUTH_TOKEN_TYPE)) {
            // 或者不是以指定字符Bearer开始的，说明是一个非法令牌，设置对应的响应status即可
            status = Status.UNAUTHENTICATED.withDescription("unknown token type");
        } else {
            // 3. 若令牌没问题，就开始进行令牌校验
            Jws<Claims> claims = null;
            String token = authorization.substring(AuthConstant.AUTH_TOKEN_TYPE.length()).trim();
            try {
                claims = parser.parseClaimsJws(token);
            } catch (JwtException e) {
                // 校验失败，则设置响应的status
                status = Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e);
            }
            if (claims != null) {
                // 校验成功，就会得到一个jws对象，从中提取出来用户名，并存入到Context中，将来在HelloService中就可以获取到这里的用户名
                Context ctx = Context.current().withValue(AuthConstant.AUTH_CLIENT_ID, claims.getBody().getSubject());
                return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
            }
        }
        serverCall.close(status, new Metadata());
        return new ServerCall.Listener<ReqT>() {
        };
    }
}
