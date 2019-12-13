package org.wisdom.db;

import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;
import org.tdf.rlp.RLPEncoding;
import org.wisdom.util.MapRLPUtil;

import java.util.Map;
import java.util.Objects;

public class ValidatorState {

    @RLP(0)
    @RLPEncoding(MapRLPUtil.LongMapEncoderDecoder.class)
    @RLPDecoding(MapRLPUtil.LongMapEncoderDecoder.class)
    private Map<String, Long> nonce;

    public Map<String, Long> getNonce() {
        return nonce;
    }

    public void setNonce(Map<String, Long> nonce) {
        this.nonce = nonce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidatorState that = (ValidatorState) o;
        return Objects.equals(nonce, that.nonce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonce);
    }

    public ValidatorState copy() {
        ValidatorState state = new ValidatorState();
        state.nonce = this.nonce;
        return state;
    }

}
