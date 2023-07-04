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
            assert clientToken != null;
            UserRole userRole = getUserRole(clientToken);
            Context context = Context.current().withValue(
                    ServerConstants.CTX_USER_ROLE,
                    userRole
            );
            return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
//            return serverCallHandler.startCall(serverCall, metadata);
        } else {
            Status status = Status.UNAUTHENTICATED.withDescription("invalid token / expired token");
            serverCall.close(status, metadata);
        }
        return new ServerCall.Listener<ReqT>() {
        };
    }

    private boolean valid(String token) {
        return Objects.nonNull(token)
                && (token.startsWith("client-secret-3") || token.startsWith("client-secret-2"));
    }

    private UserRole getUserRole(String jwt) {
        return jwt.endsWith("prime") ? UserRole.PRIME : UserRole.STANDARD;
    }
}
