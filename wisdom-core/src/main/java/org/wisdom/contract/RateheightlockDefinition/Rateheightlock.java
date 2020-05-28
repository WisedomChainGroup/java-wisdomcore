package org.wisdom.contract.RateheightlockDefinition;

import lombok.*;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.*;
import org.wisdom.contract.AnalysisContract;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

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
    @RLPDecoding(as = HexBytesTreeMap.class)
    @RLPEncoding(keyOrdering = ComparatorHexBytes.class)
    private Map<HexBytes, Extract> stateMap;

    public static class ComparatorHexBytes implements Comparator<HexBytes> {
        @Override
        public int compare(HexBytes o1, HexBytes o2) {
            return o2.compareTo(o1);
        }
    };

    public static class HexBytesTreeMap extends TreeMap<HexBytes, Extract> {
        public HexBytesTreeMap() {
            super(new Comparator<HexBytes>() {
                @Override
                public int compare(HexBytes o1, HexBytes o2) {
                    return o2.compareTo(o1);
                }
            });
        }
    }

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
        Rateheightlock rateheightlock = new Rateheightlock();
        rateheightlock.RLPdeserialization(Rlpbyte);
        return rateheightlock;
    }
}
