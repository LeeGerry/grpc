package com.me.server;

import com.me.models.Balance;
import com.me.models.BalanceCheckRequest;
import com.me.models.BankServiceGrpc;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        System.out.println(accountNumber);
        Balance balance = Balance.newBuilder().setAmount(accountNumber * 10).build();
        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }
}
