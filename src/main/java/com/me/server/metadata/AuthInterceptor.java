package com.me.server.metadata;

import io.grpc.*;

import java.util.Objects;

public class AuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler
    ) {
        String clientToken = metadata.get(ServerConstants.USER_TOKEN);
        System.out.println("Server get token from client: " + clientToken);

        if (valid(clientToken)) {
            return serverCallHandler.startCall(serverCall, metadata);
        } else {
            Status status = Status.UNAUTHENTICATED.withDescription("invalid token / expired token");
            serverCall.close(status, metadata);
        }
        return new ServerCall.Listener<ReqT>() {
        };
    }

    private boolean valid(String token) {
        return Objects.nonNull(token) && token.equals("client-secret-3");
    }
}
