package com.me.server.auth;

import com.me.server.jwt.AuthConstant;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.concurrent.Executor;

public class JwtCredential extends CallCredentials {
    private final String token;

    public JwtCredential(String token) {
        this.token = token;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                headers.put(
                        Metadata.Key.of(AuthConstant.AUTH_HEADER, Metadata.ASCII_STRING_MARSHALLER),
                        String.format("%s %s", AuthConstant.AUTH_TOKEN_TYPE, token)
                );
                metadataApplier.apply(headers);
            } catch (Throwable e) {
                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
            }
        });
    }

    @Override
    public void thisUsesUnstableApi() {

    }
}
