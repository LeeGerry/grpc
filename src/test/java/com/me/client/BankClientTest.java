package com.me.client;

import com.me.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        blockingStub = BankServiceGrpc.newBlockingStub(channel);
        bankServiceStub = BankServiceGrpc.newStub(channel);
    }

    @Test
    public void balanceTest() {
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder().setAccountNumber(50).build();
        Balance balance = blockingStub.getBalance(balanceCheckRequest);
        Assertions.assertEquals(balance.getAmount(), 500);
    }

    @Test
    public void withdrawTest() {
        WithdrawRequest request = WithdrawRequest.newBuilder().setAccountNumber(6).setAmount(40).build();
        blockingStub.withdraw(request)
                .forEachRemaining((Money money) -> {
                    int value = money.getValue();
                    System.out.println("result: " + value);
                    Assertions.assertEquals(10, value);
                });
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        WithdrawRequest request = WithdrawRequest.newBuilder().setAccountNumber(8).setAmount(40).build();
        CountDownLatch latch = new CountDownLatch(1);
        bankServiceStub.withdraw(request, new MoneyStreamingResponse(latch));
        latch.await();
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> depositRequestStreamObserver = bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            DepositRequest request = DepositRequest.newBuilder().setAccountNumber(8).setAmount(10).build();
            depositRequestStreamObserver.onNext(request);
        }
        depositRequestStreamObserver.onCompleted();
        latch.await();
    }
}
