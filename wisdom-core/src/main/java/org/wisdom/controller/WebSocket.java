package org.wisdom.controller;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;
import org.wisdom.db.WisdomRepository;
import org.wisdom.sync.TransactionHandler;
import org.wisdom.type.WebSocketMessage;
import org.wisdom.vm.abi.Parameters;
import org.wisdom.vm.abi.WASMEvent;
import org.wisdom.vm.abi.WASMTXPool;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ServerEndpoint(value = "/websocket/{id}")
@Component
@Getter
public class WebSocket {
    public static final Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CacheKey{
        private HexBytes contractAddress;
        private HexBytes stateRoot;
        private String method;
        private HexBytes parameters;
    }

    static final Cache<CacheKey, byte[]> CACHE = Caffeine.newBuilder()
            .weigher((k , v) -> {
                CacheKey key = (CacheKey) k;
                byte[] value = (byte[]) v;
                return key.contractAddress.size() + key.stateRoot.size() + key.method.length() + key.parameters.size()
                        + value.length;
            })
            // 1GB 的合约查询缓存
            .maximumWeight(1024 * 1024 * 1024)
            .build();

    public static ApplicationContext ctx;

    @JsonIgnore
    private AccountStateTrie accountTrie;

    @JsonIgnore
    private WisdomRepository repository;

    @JsonIgnore
    private Session session;

    @JsonIgnore
    private final Boolean lock = true;

    @JsonIgnore
    private Set<HexBytes> addresses;

    @JsonIgnore
    private Map<HexBytes, Boolean> transactions;

    private String id;

    @JsonIgnore
    private TransactionHandler transactionHandler;

    @JsonIgnore
    private WASMTXPool wasmtxPool;

    @JsonIgnore
    private TransactionCheck transactionCheck;

    // 收到来自这个客户端的字节数量
    private long bytesIn;

    // 发送给这个客户端的字节数量
    private long bytesOut;

    // 这个客户端发送的事务数量
    private long transactionsSend;

    // 合约查询次数
    private long contractQueries;

    // 合约查询耗时
    private long contractQueryDuration;

    // 合约查询平均耗时，单位是秒
    private double getContractQueryAverage() {
        return contractQueryDuration * 1.0 / contractQueries / 1000;
    }

    // 合约调用的次数
    private Map<String, Long> contractQueryMethodsCount = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        session.setMaxBinaryMessageBufferSize(8 * 1024 * 1024);
        this.id = id;
        this.session = session;
        this.addresses = new CopyOnWriteArraySet<>();
        this.transactions = new ConcurrentHashMap<>();
        this.wasmtxPool = ctx.getBean(WASMTXPool.class);
        this.accountTrie = ctx.getBean(AccountStateTrie.class);
        this.repository = ctx.getBean(WisdomRepository.class);
        this.transactionHandler = ctx.getBean(TransactionHandler.class);
        this.transactionCheck = ctx.getBean(TransactionCheck.class);
        clients.put(id, this);
    }

    @OnClose
    public void onClose() {
        clients.remove(this.id);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param messages 客户端发送过来的消息
     * @param session  可选的参数
     */
    @OnMessage
    public void onMessage(byte[] messages, Session session) {
        try {
            onMessageInternal(messages, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMessageInternal(byte[] messages, Session session) {
        this.bytesIn += messages.length;
        WebSocketMessage msg = RLPCodec.decode(messages, WebSocketMessage.class);
        switch (msg.getCodeEnum()) {
            // 事务监听
            case TRANSACTION_SUBSCRIBE: {
                if (msg.getBody().isRLPList()) {
                    for (RLPElement element : msg.getBody().asRLPList()) {
                        this.transactions.put(HexBytes.fromBytes(element.asBytes()), true);
                    }
                } else {
                    this.transactions.put(HexBytes.fromBytes(msg.getBody().asBytes()), true);
                }
                sendNull(msg.getNonce());
                break;
            }
            // 合约监听
            case EVENT_SUBSCRIBE: {
                this.addresses.add(HexBytes.fromBytes(msg.getBody().asBytes()));
                sendNull(msg.getNonce());
                break;
            }
            // 发送事务
            case TRANSACTION_SEND: {
                boolean isList = msg.getBody().get(0).asBoolean();
                Transaction[] txs = isList ? msg.getBody().get(1).as(Transaction[].class) :
                        new Transaction[]{msg.getBody().get(1).as(Transaction.class)};
                this.transactionsSend += txs.length;
                for (Transaction tx : txs) {
                    APIResult res = transactionCheck.TransactionFormatCheck(tx.toRPCBytes());
                    if (res.getCode() == APIResult.SUCCESS) {
                        this.wasmtxPool.collect(Collections.singleton(tx));
                        transactionHandler.broadcastTransactions(Collections.singletonList(tx));
                        broadcastPendingOrConfirm(tx, Transaction.Status.PENDING);
                    } else {
                        wasmtxPool.drop(tx, res.getMessage());
                    }
                }
                sendNull(msg.getNonce());
                break;
            }
            // 查看账户
            case ACCOUNT_QUERY: {
                byte[] pkHash = msg.getBody().asBytes();
                AccountState a = accountTrie.getTrieByBlockHash(
                        repository.getBestBlock().getHash())
                        .get(pkHash).orElse(new AccountState(pkHash));
                sendResponse(msg.getNonce(), WebSocketMessage.Code.ACCOUNT_QUERY, a);
                break;
            }
            // 查看合约
            case CONTRACT_QUERY: {
                this.contractQueries++;

                byte[] pkHash = msg.getBody().get(0).asBytes();
                String method = msg.getBody().get(1).asString();
                Parameters parameters = msg.getBody().get(2)
                        .as(Parameters.class);
                String key = HexBytes.encode(pkHash) + "#" + method;
                this.contractQueryMethodsCount.put(key, this.contractQueryMethodsCount.getOrDefault(key, 0L) + 1);
                byte[] root = accountTrie.getRootStore().get(repository.getBestBlock().getHash()).get();
                long start = System.currentTimeMillis();
                AccountState s = accountTrie.getTrie().revert(root).get(pkHash).get();

                CacheKey k = new CacheKey(
                        HexBytes.fromBytes(pkHash),
                        HexBytes.fromBytes(s.getStorageRoot()),
                        method,
                        HexBytes.fromBytes(msg.getBody().get(2).getEncoded())
                );

                byte[] result =
                        CACHE.get(k, key1 -> accountTrie.call(root, pkHash, method, parameters));
                long end = System.currentTimeMillis();
                this.contractQueryDuration += end - start;
                sendResponse(msg.getNonce(), WebSocketMessage.Code.CONTRACT_QUERY, result);
                break;
            }
        }
    }

    @SneakyThrows
    public void sendBinary(byte[] binary) {
        if (this.session == null)
            return;
        synchronized (this.lock) {
            this.bytesOut += binary.length;
            this.session.getBasicRemote().sendBinary(ByteBuffer.wrap(binary, 0, binary.length));
        }
    }

    public void sendNull(long nonce) {
        sendBinary(RLPCodec.encode(new WebSocketMessage(nonce, 0, null)));
    }

    public void sendResponse(long nonce, WebSocketMessage.Code code, Object data) {
        sendBinary(RLPCodec.encode(new WebSocketMessage(nonce, code.ordinal(), RLPElement.readRLPTree(data))));
    }

    @SneakyThrows
    public static void broadcastAsync(WebSocketMessage msg, Predicate<WebSocket> when, Consumer<WebSocket> after) {
        byte[] bin = RLPCodec.encode(msg);
        for (Map.Entry<String, WebSocket> entry : clients.entrySet()) {
            EXECUTOR.execute(() -> {
                WebSocket socket = entry.getValue();
                try {
                    if (when.test(socket)) {
                        socket.sendBinary(bin);
                        after.accept(socket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public static void broadcastTransaction(Transaction tx, RLPElement body, boolean delete) {
        WebSocketMessage msg = new WebSocketMessage(
                0,
                WebSocketMessage.Code.TRANSACTION_EMIT.ordinal(),
                body
        );
        broadcastAsync(msg,
                ws -> ws.transactions.containsKey(HexBytes.fromBytes(tx.getHash())),
                ws -> {
                    if (delete) ws.transactions.remove(HexBytes.fromBytes(tx.getHash()));
                }
        );
    }

    public static void broadcastPendingOrConfirm(Transaction tx, Transaction.Status status) {
        RLPElement body = RLPElement.readRLPTree(new WebSocketTransactionBody(tx.getHash(), status.ordinal(), null));
        broadcastTransaction(tx, body, status == Transaction.Status.CONFIRMED);
    }

    public static void broadcastDrop(Transaction tx, String reason) {
        RLPElement body = RLPElement.readRLPTree(new WebSocketTransactionBody(tx.getHash(), Transaction.Status.DROPPED.ordinal(), reason));
        broadcastTransaction(tx, body.asRLPList(), true);
    }

    public static void broadcastIncluded(Transaction tx, long height, byte[] blockHash, long gasUsed, RLPList returns, List<WASMEvent> events) {
        WebSocketTransactionBody bd =
                new WebSocketTransactionBody(
                        tx.getHash(),
                        Transaction.Status.INCLUDED.ordinal(),
                        new Object[]{height, blockHash, gasUsed, returns, events}
                );

        RLPElement body = RLPElement.readRLPTree(bd);
        broadcastTransaction(tx, body.asRLPList(), false);
    }

    public static void broadcastEvent(byte[] address, String event, RLPList outputs) {
        RLPElement bd = RLPElement.readRLPTree(new WebSocketEventBody(address, event, outputs)).asRLPList();
        HexBytes addr = HexBytes.fromBytes(address);
        broadcastAsync(
                new WebSocketMessage(0, WebSocketMessage.Code.EVENT_EMIT.ordinal(), bd),
                ws -> ws.addresses.contains(addr),
                ws -> {
                }
        );
    }
}