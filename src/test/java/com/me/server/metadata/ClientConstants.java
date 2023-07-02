package com.me.server.metadata;

import io.grpc.Metadata;

public class ClientConstants {
    public static final Metadata.Key<String> USER_TOKEN = Metadata.Key.of("user-token", Metadata.ASCII_STRING_MARSHALLER);
}
