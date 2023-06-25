package com.me.server.other;

import com.google.common.util.concurrent.Uninterruptibles;
import com.me.models.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class OtherService extends OtherServiceGrpc.OtherServiceImplBase {
    @Override
    public void deadlineTest(DeadlineRequest request, StreamObserver<DeadlineResponse> responseObserver) {
        String message = request.getRequestString();
        System.out.println("received request message: " + message);
        DeadlineResponse response = DeadlineResponse.newBuilder().setResponseString("Message from server").build();
        // 模拟3秒钟耗时操作后再发送结果
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deadlineServerStreamTest(DeadlineServerStreamRequest request, StreamObserver<DeadlineServerStreamResponse> responseObserver) {
        int size = request.getSize();
        System.out.println("收到Client请求 " + size + " 条message");
        int i = 0;
        for (; i < size; i++) {
            DeadlineServerStreamResponse response = DeadlineServerStreamResponse
                    .newBuilder()
                    .setMessage("message from server " + i)
                    .build();
            if (Context.current().isCancelled()) break;
            responseObserver.onNext(response);
            System.out.println("第" + i + "条message已发送");
            // Sleep 2 seconds after sending each message
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        }
        responseObserver.onCompleted();
        System.out.println("发送完成，共发送 " + i + "条message");
    }
}
