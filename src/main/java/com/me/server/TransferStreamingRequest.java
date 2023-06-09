package com.me.server;

import com.me.models.Account;
import com.me.models.TransferRequest;
import com.me.models.TransferResponse;
import com.me.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {
    private final StreamObserver<TransferResponse> transferResponseStreamObserver;

    public TransferStreamingRequest(StreamObserver<TransferResponse> responseObserver) {
        transferResponseStreamObserver = responseObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        int fromAccount = transferRequest.getFromAccount();
        int toAccount = transferRequest.getToAccount();
        int amount = transferRequest.getAmount();
        int balance = AccountDatabase.getBalance(fromAccount);
        TransferStatus status = TransferStatus.FAILED;
        if (balance >= amount && fromAccount != toAccount) {
            AccountDatabase.deductBalance(fromAccount, amount);
            AccountDatabase.addBalance(toAccount, amount);
            status = TransferStatus.SUCCESS;
        }
        Account fromAccountInfo = Account.newBuilder().setAccountNumber(fromAccount).setAmount(AccountDatabase.getBalance(fromAccount)).build();
        Account toAccountInfo = Account.newBuilder().setAccountNumber(toAccount).setAmount(AccountDatabase.getBalance(toAccount)).build();
        TransferResponse transferResponse = TransferResponse.newBuilder()
                .setStatus(status)
                .addAccount(fromAccountInfo)
                .addAccount(toAccountInfo)
                .build();

        transferResponseStreamObserver.onNext(transferResponse);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        AccountDatabase.printAccountDetails();
        transferResponseStreamObserver.onCompleted();
    }
}
