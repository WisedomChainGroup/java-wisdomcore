package org.wisdom.db;

import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;

import java.util.Map;

public class ValidatorState {
    @RLP(0)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Long> nonce;

    public Map<byte[], Long> getNonce() {
        return nonce;
    }

    public void setNonce(ByteArrayMap<Long> nonce) {
        this.nonce = nonce;
    }

    public ValidatorState() {
        nonce = new ByteArrayMap<>();
    }

    public ValidatorState(Map<byte[], Long> nonce) {
        this.nonce = nonce;
    }

    public ValidatorState copy() {
        ValidatorState state = new ValidatorState();
        state.nonce = new ByteArrayMap<>(this.nonce);
        return state;
    }

}
