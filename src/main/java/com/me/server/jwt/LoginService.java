package com.me.server.jwt;

import come.me.model.LoginBody;
import come.me.model.LoginResponse;
import come.me.model.LoginServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;

public class LoginService extends LoginServiceGrpc.LoginServiceImplBase {
    @Override
    public void login(LoginBody request, StreamObserver<LoginResponse> responseObserver) {
        String userName = request.getUsername();
        String password = request.getPassword();
        if (userName.equals("Jack") && password.equals("test")) {
            System.out.println("login success");
            String jwtToken = Jwts.builder().setSubject(userName).signWith(AuthConstant.JWT_KEY).compact();
            responseObserver.onNext(LoginResponse.newBuilder().setToken(jwtToken).build());
            responseObserver.onCompleted();
        } else {
            System.out.println("login error");
            responseObserver.onNext(LoginResponse.newBuilder().setToken("error").build());
            responseObserver.onCompleted();
        }
    }

}
