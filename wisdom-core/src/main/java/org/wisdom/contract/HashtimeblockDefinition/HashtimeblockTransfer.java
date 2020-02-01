package org.wisdom.contract.HashtimeblockDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashtimeblockTransfer implements AnalysisContract {
    @RLP(0)
    private Long value;
    @RLP(1)
    private byte[] hashresult;
    @RLP(2)
    private Long timestamp;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashtimeblockTransfer hashtimeblockTransfer = RLPElement.fromEncoded(payload).as(HashtimeblockTransfer.class);
        if (hashtimeblockTransfer == null) {
            return false;
        }
        this.value = hashtimeblockTransfer.getValue();
        this.hashresult = hashtimeblockTransfer.getHashresult();
        this.timestamp = hashtimeblockTransfer.getTimestamp();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashtimeblockTransfer.builder()
                .value(this.getValue())
                .hashresult(this.hashresult)
                .timestamp(this.timestamp).build());
    }

    public static HashtimeblockTransfer getHashtimeblockTransfer(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(HashtimeblockTransfer.class);
    }
}
