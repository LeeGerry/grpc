package com.me.server.other;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class OtherServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6666)
                .addService(new OtherService())
                .build();
        server.start();
        server.awaitTermination();
    }
}
