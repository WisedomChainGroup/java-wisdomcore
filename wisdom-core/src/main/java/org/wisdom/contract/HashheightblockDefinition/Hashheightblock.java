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
public class Hashheightblock implements AnalysisContract {
    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private byte[] pubkeyHash;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Hashheightblock hashheightblock = RLPElement.fromEncoded(payload).as(Hashheightblock.class);
        if (hashheightblock == null) {
            return false;
        }
        this.assetHash = hashheightblock.getAssetHash();
        this.pubkeyHash = hashheightblock.getPubkeyHash();
        return true;
    }


    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Hashheightblock.builder()
                .assetHash(this.getAssetHash())
                .pubkeyHash(this.getPubkeyHash()).build());
    }

    public static Hashheightblock getHashheightblock(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Hashheightblock.class);
    }
}
