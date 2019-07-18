package org.wisdom.p2p;

import io.grpc.stub.StreamObserver;

public class Wisdom extends WisdomGrpc.WisdomImplBase {
    @Override
    public void entry(WisdomOuterClass.Message request, StreamObserver<WisdomOuterClass.Message> responseObserver) {
        super.entry(request, responseObserver);
    }
}
