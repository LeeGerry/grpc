package com.me.server.other;

import com.me.models.DeadlineRequest;
import com.me.models.DeadlineResponse;
import com.me.models.OtherServiceGrpc;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

import static io.grpc.Status.DEADLINE_EXCEEDED;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OtherServiceTest {
    private OtherServiceGrpc.OtherServiceBlockingStub blockingStub;
    private OtherServiceGrpc.OtherServiceStub stub;

    @BeforeAll
    public void setup() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6666)
                .usePlaintext()
                .build();
        blockingStub = OtherServiceGrpc.newBlockingStub(channel);
        stub = OtherServiceGrpc.newStub(channel);
    }

    @Test
    public void testDeadlineSuccessfully() {
        DeadlineRequest request = DeadlineRequest
                .newBuilder()
                .setRequestString("message from client")
                .build();
        DeadlineResponse response = blockingStub.deadlineTest(request);
        System.out.println("From server: " + response.getResponseString());
    }

    @Test
    public void testDeadlineTimeout() {
        DeadlineRequest request = DeadlineRequest
                .newBuilder()
                .setRequestString("message from client")
                .build();
        StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            DeadlineResponse response = blockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .deadlineTest(request);
            System.out.println("From server: " + response.getResponseString());
        });
        Assertions.assertEquals(DEADLINE_EXCEEDED.getCode(), thrown.getStatus().getCode());
    }
}