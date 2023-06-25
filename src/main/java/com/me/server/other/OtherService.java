package com.me.server.other;

import com.google.common.util.concurrent.Uninterruptibles;
import com.me.models.DeadlineRequest;
import com.me.models.DeadlineResponse;
import com.me.models.OtherServiceGrpc;
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
}
