package com.me.server.jwt;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;

public class HelloAuthServer {
    Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        HelloAuthServer server = new HelloAuthServer();
        server.start();
        server.blockUntilShutdown();
    }

    public void start() throws IOException {
        int port = 6000;
        server = ServerBuilder.forPort(port)
                .addService(new LoginService()) // 添加login service, 登录的时候不需要拦截器
                // 添加Hello service时，增加拦截器，进行验证
                .addService(ServerInterceptors.intercept(new HelloService(), new AuthInterceptor()))
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private void stop() {
        if (server != null) server.shutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) server.awaitTermination();
    }
}
