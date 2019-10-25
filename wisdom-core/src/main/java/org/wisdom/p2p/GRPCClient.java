package org.wisdom.p2p;

import com.google.protobuf.AbstractMessage;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

@Component
public class GRPCClient {

    public GRPCClient withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Value("${p2p.enable-message-log}")
    private boolean enableMessageLog;

    private Executor executor;

    private ConcurrentMap<HostPort, ManagedChannel> channelCache;

    private static final int RPC_TIMEOUT = 3;

    private Peer self;

    public long getNonce() {
        return nonce.incrementAndGet();
    }

    private AtomicLong nonce;

    private int timeout;

    public GRPCClient withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    private ManagedChannel getChannel(HostPort hostPort){
        ManagedChannel channel = channelCache.get(hostPort);
        if (channel != null && !channel.isShutdown()){
            return channel;
        }
        ManagedChannel ch = ManagedChannelBuilder.forAddress(hostPort.getHost(), hostPort.getPort()
        ).usePlaintext().build();
        channelCache.put(hostPort, ch);
        return ch;
    }

    public GRPCClient(){
        this.nonce = new AtomicLong();
        this.timeout = RPC_TIMEOUT;
        this.executor = Executors.newCachedThreadPool();
        this.channelCache = new ConcurrentLinkedHashMap.Builder<HostPort, ManagedChannel>().maximumWeightedCapacity(PeersCache.MAX_PEERS * 2).build();
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

        private boolean enableExceptionStackTrace;

        public SimpleObserver withExceptionStackTrance(boolean enableExceptionStackTrance) {
            this.enableExceptionStackTrace = enableExceptionStackTrace;
            return this;
        }

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
        public void onCompleted() { }
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
        ManagedChannel ch = getChannel(new HostPort(host, port));

        WisdomGrpc.WisdomBlockingStub stub = WisdomGrpc.newBlockingStub(
                ch).withDeadlineAfter(timeout, TimeUnit.SECONDS);
        
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
        ManagedChannel ch = getChannel(new HostPort(host, port));
        WisdomGrpc.WisdomStub stub = WisdomGrpc.newStub(
                ch).withDeadlineAfter(timeout, TimeUnit.SECONDS);

        stub.entry(msg, new SimpleObserver(ch, function).withExceptionStackTrance(enableMessageLog));
    }
}
