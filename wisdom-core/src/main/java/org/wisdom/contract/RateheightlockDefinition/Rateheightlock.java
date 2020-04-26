package org.wisdom.contract.RateheightlockDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPDecoding;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rateheightlock implements AnalysisContract {

    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private long onetimedepositmultiple;
    @RLP(2)
    private int withdrawperiodheight;
    @RLP(3)
    private BigDecimal withdrawrate;
    @RLP(4)
    private byte[] dest;
    @RLP(5)
    @RLPDecoding(as = Extract.class)
    private Extract extract;

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Rateheightlock.builder()
                .assetHash(this.assetHash)
                .onetimedepositmultiple(this.onetimedepositmultiple)
                .withdrawperiodheight(this.withdrawperiodheight)
                .withdrawrate(this.withdrawrate)
                .dest(this.dest)
                .extract(this.extract).build());
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Rateheightlock rateheightlock = RLPElement.fromEncoded(payload).as(Rateheightlock.class);
        if (rateheightlock == null) {
            return false;
        }
        this.assetHash = rateheightlock.getAssetHash();
        this.onetimedepositmultiple = rateheightlock.getOnetimedepositmultiple();
        this.withdrawperiodheight = rateheightlock.getWithdrawperiodheight();
        this.withdrawrate = rateheightlock.getWithdrawrate();
        this.dest = rateheightlock.getDest();
        this.extract = rateheightlock.extract;
        return true;
    }
}
