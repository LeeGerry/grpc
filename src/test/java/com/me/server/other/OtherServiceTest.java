package com.me.server.other;

import com.me.models.*;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
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
                .intercept(new DeadlineInterceptor())
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

    @Test
    public void testDeadlineForServerStreamSuccessfully() throws InterruptedException {
        DeadlineServerStreamRequest request = DeadlineServerStreamRequest
                .newBuilder()
                .setSize(5)
                .build();
        CountDownLatch latch = new CountDownLatch(1);
        stub.deadlineServerStreamTest(request, new StreamObserver<DeadlineServerStreamResponse>() {
            @Override
            public void onNext(DeadlineServerStreamResponse deadlineServerStreamResponse) {
                System.out.println("Client 收到: " + deadlineServerStreamResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Client 收到错误: " + throwable.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Client 收到结束");
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void testDeadlineForServerStreamTimeout() throws InterruptedException {
        DeadlineServerStreamRequest request = DeadlineServerStreamRequest
                .newBuilder()
                .setSize(5)
                .build();
        CountDownLatch latch = new CountDownLatch(1);
        stub.deadlineServerStreamTest(request, new StreamObserver<DeadlineServerStreamResponse>() {
                    @Override
                    public void onNext(DeadlineServerStreamResponse deadlineServerStreamResponse) {
                        System.out.println("Client 收到: " + deadlineServerStreamResponse.getMessage());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // 在发送请求时设置4秒的deadline，
                        // request中请求5条message，Server每发送一个会sleep 2秒。
                        // 所以在收到2条后就就达到了4秒超时设定，会收到throwable: DEADLINE_EXCEEDED
                        System.out.println("Client 收到错误: " + throwable.getMessage());
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Client 收到结束");
                        latch.countDown();
                    }
                });
        latch.await();
    }
}