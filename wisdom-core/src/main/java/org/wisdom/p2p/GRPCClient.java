package org.wisdom.p2p;

import com.google.protobuf.AbstractMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Component
public class GRPCClient {

    public GRPCClient withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    private Executor executor;

    private static final int RPC_TIMEOUT = 3;

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

    public WisdomOuterClass.Message buildMessage(long ttl, AbstractMessage msg){
        return Util.buildMessage(self, nonce.incrementAndGet(), ttl, msg);
    }

    private static class SimpleObserver implements StreamObserver<WisdomOuterClass.Message> {

        private ManagedChannel channel;

        private BiConsumer<WisdomOuterClass.Message, Throwable> function;

        public SimpleObserver(ManagedChannel channel, BiConsumer<WisdomOuterClass.Message, Throwable> function) {
            this.channel = channel;
            this.function = function;
        }

        @Override
        public void onNext(WisdomOuterClass.Message value) {
            function.accept(value, null);
        }

        @Override
        public void onError(Throwable t) {
            function.accept(null, t);
            channel.shutdown();
        }

        @Override
        public void onCompleted() {
            channel.shutdown();
        }
    }

    public  CompletableFuture<WisdomOuterClass.Message> dialWithTTL(String host, int port, long ttl, AbstractMessage msg){
        if(msg instanceof WisdomOuterClass.Message){
            return dial(host, port, (WisdomOuterClass.Message) msg);
        }
        return dial(host, port, buildMessage(ttl, msg));
    }

    public void dialAsyncWithTTL(String host, int port, long ttl, AbstractMessage msg, BiConsumer<WisdomOuterClass.Message, Throwable> function){
        if(msg instanceof WisdomOuterClass.Message){
            dialAsync(host, port, (WisdomOuterClass.Message) msg, function);
            return;
        }
        dialAsync(host, port, buildMessage(ttl, msg), function);
    }

    private CompletableFuture<WisdomOuterClass.Message> dial(String host, int port, WisdomOuterClass.Message msg) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress(host, port
        ).usePlaintext().build();

        WisdomGrpc.WisdomBlockingStub stub = WisdomGrpc.newBlockingStub(
                ch).withDeadlineAfter(RPC_TIMEOUT, TimeUnit.SECONDS);

        return CompletableFuture
                .supplyAsync(() -> {
                    try{
                        return stub.entry(msg);
                    }catch (Exception e){
                        throw new RuntimeException(e.getMessage());
                    } finally {
                        ch.shutdown();
                    }
                }, executor);
    }

    private void dialAsync(String host, int port, WisdomOuterClass.Message msg, BiConsumer<WisdomOuterClass.Message, Throwable> function) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress(host, port
        ).usePlaintext().build();

        WisdomGrpc.WisdomStub stub = WisdomGrpc.newStub(
                ch).withDeadlineAfter(RPC_TIMEOUT, TimeUnit.SECONDS);

        stub.entry(msg, new SimpleObserver(ch, function));
    }
}
