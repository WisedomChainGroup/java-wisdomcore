package org.wisdom.p2p;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GRPCClient {

    public GRPCClient withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    private Executor executor;

    private static final int RPC_TIMEOUT = 5;

    private Peer self;

    public long getNonce() {
        return nonce.incrementAndGet();
    }

    private AtomicLong nonce;


    public GRPCClient(){
        this.nonce = new AtomicLong();
        this.executor = Executors.newCachedThreadPool();
    }

    public GRPCClient(Peer self){
        this();
        this.self = self;
    }

    public GRPCClient withSelf(Peer self){
        this.self = self;
        return this;
    }

    public WisdomOuterClass.Message buildMessage(long ttl, Object msg){
        return Util.buildMessage(self, nonce.incrementAndGet(), ttl, msg);
    }

    private static class SimpleObserver implements StreamObserver<WisdomOuterClass.Message> {
        private WisdomOuterClass.Message response;

        private ManagedChannel channel;

        private Throwable exception;

        public SimpleObserver(ManagedChannel channel) {
            this.channel = channel;
        }

        public WisdomOuterClass.Message getResponse() {
            return response;
        }

        public Throwable getException() {
            return exception;
        }

        @Override
        public void onNext(WisdomOuterClass.Message value) {
            response = value;
        }

        @Override
        public void onError(Throwable t) {
            this.exception = t;
        }

        @Override
        public void onCompleted() {
            channel.shutdown();
        }
    }

    public  CompletableFuture<WisdomOuterClass.Message> dialWithTTL(String host, int port, long ttl, Object msg){
        return dial(host, port, buildMessage(ttl, msg));
    }

    private CompletableFuture<WisdomOuterClass.Message> dial(String host, int port, WisdomOuterClass.Message msg) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress(host, port
        ).usePlaintext().build();

        WisdomGrpc.WisdomStub stub = WisdomGrpc.newStub(
                ch);

        return CompletableFuture
                .supplyAsync(() -> {
                    SimpleObserver observer = new SimpleObserver(ch);
                    stub.entry(msg, observer);
                    try {
                        ch.awaitTermination(RPC_TIMEOUT, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException("grpc timeout");
                    }
                    if (observer.getException() != null) {
                        throw new RuntimeException(observer.getException().getMessage());
                    } else {
                        return observer.getResponse();
                    }
                }, executor);
    }
}
