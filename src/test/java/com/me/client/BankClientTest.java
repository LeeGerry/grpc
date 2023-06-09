package com.me.client;

import com.me.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    @BeforeAll
    public void setup() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        blockingStub = BankServiceGrpc.newBlockingStub(channel);
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
}
