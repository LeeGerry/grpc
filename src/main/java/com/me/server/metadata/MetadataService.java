package com.me.server.metadata;

import com.me.models.MetaRequest;
import com.me.models.MetaResponse;
import com.me.models.MetadataServiceGrpc;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

import static com.me.server.metadata.ServerConstants.CTX_USER_ROLE;

public class MetadataService extends MetadataServiceGrpc.MetadataServiceImplBase {
    @Override
    public void metadataTest(MetaRequest request, StreamObserver<MetaResponse> responseObserver) {
        Context.Key<UserRole> key = Context.key("user-role");
        UserRole userRole = CTX_USER_ROLE.get(Context.current());
        System.out.println("Service: " + userRole);
        MetaResponse response = MetaResponse.newBuilder().setResult("result_" + userRole).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
