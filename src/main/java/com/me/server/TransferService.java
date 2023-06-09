package com.me.server;

import com.me.models.TransferRequest;
import com.me.models.TransferResponse;
import com.me.models.TransferServiceGrpc;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        return new TransferStreamingRequest(responseObserver);
    }

}
