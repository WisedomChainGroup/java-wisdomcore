package org.wisdom.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    public static final int EVENT_EMIT = 1;
    public static final int EVENT_SUBSCRIBE = 2;
    public static final int TRANSACTION_EMIT = 3;
    public static final int TRANSACTION_SUBSCRIBE = 4;


    private int nonce;
    private int type;
    private RLPElement body;

    public static WebSocketMessage event(byte[] pkHash, String name, RLPList data){
        return new WebSocketMessage(0, EVENT_EMIT, RLPElement.readRLPTree(new Object[]{pkHash, name, data}));
    }
}
