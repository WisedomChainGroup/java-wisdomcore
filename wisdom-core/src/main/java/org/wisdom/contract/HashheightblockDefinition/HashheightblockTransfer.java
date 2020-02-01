package org.wisdom.contract.HashheightblockDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashheightblockTransfer implements AnalysisContract {
    @RLP(0)
    private Long value;
    @RLP(1)
    private byte[] hashresult;
    @RLP(2)
    private Long height;

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashheightblockTransfer.builder()
                                .hashresult(this.hashresult)
                                .height(this.height)
                                .value(this.value));
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashheightblockTransfer hashheightblockTransfer = RLPCodec.decode(payload,HashheightblockTransfer.class);
        if(hashheightblockTransfer == null){
            return false;
        }
        this.value = hashheightblockTransfer.value;
        this.hashresult = hashheightblockTransfer.hashresult;
        this.height = hashheightblockTransfer.height;
        return true;
    }
}
