package com.me.server.auth;

import com.google.protobuf.StringValue;
import come.me.model.HelloServiceGrpc;
import come.me.model.LoginBody;
import come.me.model.LoginResponse;
import come.me.model.LoginServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.grpc.Status.UNAUTHENTICATED;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthClientTest {
    private ManagedChannel channel;

    @BeforeAll
    public void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 6000)
                .usePlaintext()
                .build();
    }

    @Test
    public void testLoginSuccess() {
        LoginServiceGrpc.LoginServiceBlockingStub loginStub = LoginServiceGrpc.newBlockingStub(channel);
        LoginBody request = LoginBody.newBuilder().setUsername("Jack").setPassword("test").build();
        LoginResponse response = loginStub.login(request);
        String token = response.getToken();
        System.out.println(token);
        Assertions.assertEquals(token.split("\\.").length, 3);
    }

    @Test
    public void testLoginFailed() {
        LoginServiceGrpc.LoginServiceBlockingStub loginStub = LoginServiceGrpc.newBlockingStub(channel);
        LoginBody request = LoginBody.newBuilder().setUsername("Jack1").setPassword("test").build();
        StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            LoginResponse response = loginStub.login(request);
        });
        Assertions.assertEquals(thrown.getStatus().getDescription(), "login error");
    }

    @Test
    public void testHelloServiceFailed() {
        StringValue request = StringValue.newBuilder().setValue("message from Client A").build();
        HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel);
        StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            StringValue response = stub.sayHello(request);
        });
        Assertions.assertEquals(UNAUTHENTICATED.getCode(), thrown.getStatus().getCode());
    }

    @Test
    void testHelloServiceSuccess() {
        // token from login service
        // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKYWNrIn0.b33DPJfgbn99Zf-rbc44LLKkQ-F8YUPWD0u2ZTIv_Ck
        StringValue request = StringValue.newBuilder().setValue("message from Client A").build();
        HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel);
        StringValue response = stub
                .withCallCredentials(new JwtCredential("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKYWNrIn0.b33DPJfgbn99Zf-rbc44LLKkQ-F8YUPWD0u2ZTIv_Ck"))
                .sayHello(request);
        String result = response.getValue();
        System.out.println(result);
        Assertions.assertEquals(result, "Jack say hello: message from Client A");
    }
}
