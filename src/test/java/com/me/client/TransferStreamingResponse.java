package com.me.client;

import com.me.models.TransferResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class TransferStreamingResponse implements StreamObserver<TransferResponse> {
    private CountDownLatch latch;

    public TransferStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(TransferResponse transferResponse) {
        System.out.println("status: " + transferResponse.getStatus());
        transferResponse.getAccountList()
                .stream()
                .map(account -> account.getAccountNumber() + ": " + account.getAmount())
                .forEach(System.out::println);
        System.out.println("----------------------------");
    }

    @Override
    public void onError(Throwable throwable) {
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All transfers done!");
        latch.countDown();
    }
}
