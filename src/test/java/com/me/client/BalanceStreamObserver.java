package com.me.client;

import com.me.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {
    private final CountDownLatch latch;

    public BalanceStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Balance balance) {
        System.out.println("Balance : " + balance.getAmount());
    }

    @Override
    public void onError(Throwable throwable) {
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server is done!");
        latch.countDown();
    }
}
