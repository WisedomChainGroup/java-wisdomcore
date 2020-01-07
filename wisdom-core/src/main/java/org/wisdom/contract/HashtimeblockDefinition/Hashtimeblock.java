package org.wisdom.contract.HashtimeblockDefinition;


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
public class Hashtimeblock implements AnalysisContract {
    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private byte[] pubkeyHash;
    @RLP(2)
    private byte[] hashresult;
    @RLP(3)
    private Long timestamp;


    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Hashtimeblock.builder()
                                .assetHash(this.getAssetHash())
                                .pubkeyHash(this.getPubkeyHash())
                                .hashresult(this.getHashresult())
                                .timestamp(this.getTimestamp()));
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Hashtimeblock hashtimeblock = RLPCodec.decode(payload,Hashtimeblock.class);
        if(hashtimeblock == null){
            return false;
        }
        this.assetHash = hashtimeblock.getAssetHash();
        this.pubkeyHash = hashtimeblock.getPubkeyHash();
        this.hashresult = hashtimeblock.getHashresult();
        this.timestamp = hashtimeblock.getTimestamp();
        return true;
    }
}
