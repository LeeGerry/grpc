package com.me.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.me.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

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
    public void withdrawAsyncTest() {
        WithdrawRequest request = WithdrawRequest.newBuilder().setAccountNumber(8).setAmount(40).build();
        bankServiceStub.withdraw(request, new StreamObserver<Money>() {
            @Override
            public void onNext(Money money) {
                System.out.println("received: " + money.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("server is done!");
            }
        });
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
    }
}
