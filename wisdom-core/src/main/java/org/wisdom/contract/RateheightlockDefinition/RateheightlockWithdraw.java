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
public class RateheightlockWithdraw implements AnalysisContract {
    @RLP(0)
    private byte[] deposithash;
    @RLP(1)
    private byte[] to;

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(RateheightlockWithdraw.builder()
                .deposithash(this.deposithash)
                .to(this.to).build());
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        RateheightlockWithdraw rateheightlockWithdraw = RLPElement.fromEncoded(payload).as(RateheightlockWithdraw.class);
        if (rateheightlockWithdraw == null) {
            return false;
        }
        this.deposithash = rateheightlockWithdraw.getDeposithash();
        this.to = rateheightlockWithdraw.getTo();
        return true;
    }
}
