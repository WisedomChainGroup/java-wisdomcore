package org.wisdom.ws;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPList;
import org.wisdom.type.WebSocketMessage;
import org.wisdom.util.CopyOnWriteMap;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * websocket 协议通信过程
 * 1. 客户端生成 uuid，通过 /websocket/{uuid} 连接到服务器
 * 2. websocket 消息内容是二进制的 rlp 编码
 */
@ServerEndpoint(value = "/websocket/{id}")
@Component
public class WebSocket {
    private static final Map<String, WebSocket> clients = new CopyOnWriteMap<>();
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
    private Session session;
    private final Boolean lock = true;

    private Set<HexBytes> contracts;
    private Map<HexBytes, Boolean> transactions;
    private String id;

    public void init() {

    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        this.id = id;
        this.session = session;
        this.contracts = new CopyOnWriteArraySet<>();
        this.transactions = new ConcurrentHashMap<>();
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
        WebSocketMessage msg = RLPCodec.decode(messages, WebSocketMessage.class);
        switch (msg.getType()) {
            // 事务监听
            case WebSocketMessage.TRANSACTION_SUBSCRIBE: {
                this.transactions.put(HexBytes.fromBytes(msg.getBody().asBytes()), true);
                sendSuccess(msg.getNonce());
                break;
            }
            // 合约监听
            case WebSocketMessage.EVENT_SUBSCRIBE: {
                this.contracts.add(HexBytes.fromBytes(msg.getBody().asBytes()));
                sendSuccess(msg.getNonce());
                break;
            }
        }
    }

    @SneakyThrows
    public void sendBinary(byte[] binary) {
        if (this.session == null)
            return;
        synchronized (this.lock) {
            this.session.getBasicRemote().sendBinary(ByteBuffer.wrap(binary, 0, binary.length));
        }
    }

    public void sendSuccess(int nonce) {
        sendBinary(RLPCodec.encode(new WebSocketMessage(nonce, 0, null)));
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

    // 广播合约事件
    public static void broadcastEvent(byte[] pkHash, String name, RLPList outputs) {
        HexBytes h = HexBytes.fromBytes(pkHash);
        broadcastAsync(
                WebSocketMessage.event(pkHash, name, outputs),
                w -> w.contracts.contains(h),
                w -> {
                }
        );
    }

}
