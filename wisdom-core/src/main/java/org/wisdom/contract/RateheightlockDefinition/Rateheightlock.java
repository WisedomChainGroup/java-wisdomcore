package org.wisdom.contract.RateheightlockDefinition;

import lombok.*;
import org.tdf.rlp.*;
import org.wisdom.contract.AnalysisContract;

import java.util.Map;

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
    private String withdrawrate;
    @RLP(4)
    private byte[] dest;
    @RLP(5)
    private Map<byte[],Extract> stateMap;

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Rateheightlock.builder()
                .assetHash(this.assetHash)
                .onetimedepositmultiple(this.onetimedepositmultiple)
                .withdrawperiodheight(this.withdrawperiodheight)
                .withdrawrate(this.withdrawrate)
                .dest(this.dest)
                .stateMap(this.stateMap).build());
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
        this.stateMap = rateheightlock.getStateMap();
        return true;
    }

    public static Rateheightlock getRateheightlock(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Rateheightlock.class);
    }
}
