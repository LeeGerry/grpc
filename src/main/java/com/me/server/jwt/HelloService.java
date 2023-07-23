package com.me.server.jwt;

import com.google.protobuf.StringValue;
import come.me.model.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void sayHello(StringValue request, StreamObserver<StringValue> responseObserver) {
        //表示当前访问用户的ID，这个用户ID是在拦截器中存入进来的
        String clientId = AuthConstant.AUTH_CLIENT_ID.get();
        responseObserver.onNext(StringValue.newBuilder().setValue(clientId + " say hello: " + request.getValue()).build());
        responseObserver.onCompleted();
    }
}
