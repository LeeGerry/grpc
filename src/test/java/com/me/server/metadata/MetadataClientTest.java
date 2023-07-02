package com.me.server.metadata;

import com.me.models.MetaRequest;
import com.me.models.MetaResponse;
import com.me.models.MetadataServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.grpc.Status.UNAUTHENTICATED;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataClientTest {
    private MetadataServiceGrpc.MetadataServiceBlockingStub blockingStub;

    @BeforeAll
    public void setup() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6667)
                .usePlaintext()
                .build();
        blockingStub = MetadataServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void testInvalidAuth() {
        MetaRequest req = MetaRequest
                .newBuilder()
                .build();
        StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            MetaResponse response = blockingStub
                    .withCallCredentials(new ClientSessionToken("client-secret-invalid"))
                    .metadataTest(req);
        });
        Assertions.assertEquals(UNAUTHENTICATED.getCode(), thrown.getStatus().getCode());
    }

    @Test
    public void testValidAuth() {
        MetaRequest req = MetaRequest
                .newBuilder()
                .build();
        MetaResponse metaResponse = blockingStub
                .withCallCredentials(new ClientSessionToken("client-secret-3"))
                .metadataTest(req);
        Assertions.assertEquals(metaResponse.getResult(), "result");
    }
}
