package com.me.server.metadata;

import io.grpc.Context;
import io.grpc.Metadata;

public class ServerConstants {
    public static final Metadata.Key<String> USER_TOKEN = Metadata.Key.of("user-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<UserRole> CTX_USER_ROLE = Context.key("user-role");
    public static final Context.Key<UserRole> CTX_USER_ROLE_1 = Context.key("user-role");
}
