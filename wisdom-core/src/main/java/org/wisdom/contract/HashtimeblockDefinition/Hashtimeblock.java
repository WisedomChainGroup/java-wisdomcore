package org.wisdom.contract.HashtimeblockDefinition;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtimeblock implements AnalysisContract {
    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private byte[] pubkeyHash;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Hashtimeblock hashtimeblock = RLPElement.fromEncoded(payload).as(Hashtimeblock.class);
        if (hashtimeblock == null) {
            return false;
        }
        this.assetHash = hashtimeblock.getAssetHash();
        this.pubkeyHash = hashtimeblock.getPubkeyHash();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Hashtimeblock.builder()
                .assetHash(this.assetHash)
                .pubkeyHash(this.pubkeyHash).build());
    }

    private String HexAssetHash() {
        return Hex.toHexString(this.assetHash);
    }

    public static Hashtimeblock getHashtimeblock(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Hashtimeblock.class);
    }
}
