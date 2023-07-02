package com.me.server.metadata;

import com.me.models.MetaRequest;
import com.me.models.MetaResponse;
import com.me.models.MetadataServiceGrpc;
import io.grpc.stub.StreamObserver;

public class MetadataService extends MetadataServiceGrpc.MetadataServiceImplBase {
    @Override
    public void metadataTest(MetaRequest request, StreamObserver<MetaResponse> responseObserver) {
        responseObserver.onNext(MetaResponse.newBuilder().setResult("result").build());
        responseObserver.onCompleted();
    }
}
