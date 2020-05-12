package org.wisdom.contract.RateheightlockDefinition;

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
public class RateheightlockDeposit implements AnalysisContract {
    @RLP(0)
    private long value;

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(RateheightlockDeposit.builder()
                .value(this.value).build());
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        RateheightlockDeposit rateheightlockDeposit = RLPElement.fromEncoded(payload).as(RateheightlockDeposit.class);
        if (rateheightlockDeposit == null) {
            return false;
        }
        this.value = rateheightlockDeposit.getValue();
        return true;
    }

    public static RateheightlockDeposit getRateheightlockDeposit(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(RateheightlockDeposit.class);
    }
}
