package org.wisdom.contract.HashheightblockDefinition;

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
public class HashheightblockTransfer implements AnalysisContract {
    @RLP(0)
    private long value;
    @RLP(1)
    private byte[] hashresult;
    @RLP(2)
    private long height;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashheightblockTransfer hashheightblockTransfer = RLPElement.fromEncoded(payload).as(HashheightblockTransfer.class);
        if (hashheightblockTransfer == null) {
            return false;
        }
        this.value = hashheightblockTransfer.value;
        this.hashresult = hashheightblockTransfer.hashresult;
        this.height = hashheightblockTransfer.height;
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashheightblockTransfer.builder()
                .hashresult(this.hashresult)
                .height(this.height)
                .value(this.value).build());
    }

    public static HashheightblockTransfer getHashheightblockTransfer(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(HashheightblockTransfer.class);
    }
}
