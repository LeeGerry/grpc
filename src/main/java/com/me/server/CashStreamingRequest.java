package com.me.server;

import com.me.models.Balance;
import com.me.models.DepositRequest;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private final StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;

    public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver) {
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext(DepositRequest depositRequest) {
        int accountNumber = depositRequest.getAccountNumber();
        int amount = depositRequest.getAmount();
        accountBalance = AccountDatabase.addBalance(accountNumber, amount);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("error: " + throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder().setAmount(accountBalance).build();
        balanceStreamObserver.onNext(balance);
        balanceStreamObserver.onCompleted();
    }
}
