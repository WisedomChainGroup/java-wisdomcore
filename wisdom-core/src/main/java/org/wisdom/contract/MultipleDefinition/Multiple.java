package org.wisdom.contract.MultipleDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multiple implements AnalysisContract {

    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private int max;
    @RLP(2)
    private int min;
    @RLP(3)
    private List<byte[]> pubList;//公钥
    @RLP(4)
    private long amount;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Multiple multiple = RLPElement.fromEncoded(payload).as(Multiple.class);
        if (multiple == null) {
            return false;
        }
        this.assetHash = multiple.getAssetHash();
        this.max = multiple.getMax();
        this.min = multiple.getMin();
        this.pubList = multiple.getPubList();
        this.amount = multiple.getAmount();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Multiple.builder()
                .assetHash(this.assetHash)
                .max(this.max)
                .min(this.min)
                .pubList(this.pubList)
                .amount(this.amount).build());
    }

    public static Multiple getMultiple(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Multiple.class);
    }
}
