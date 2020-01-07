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
public class HashtimeblockGet implements AnalysisContract {
    @RLP(0)
    private byte[] transferhash;
    @RLP(1)
    private String origintext;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashtimeblockGet.builder()
                                .transferhash(this.getTransferhash())
                                .origintext(this.getOrigintext()));
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashtimeblockGet hashtimeblockGet = RLPCodec.decode(payload,HashtimeblockGet.class);
        if(hashtimeblockGet == null){
            return false;
        }
        this.transferhash = hashtimeblockGet.getTransferhash();
        this.origintext = hashtimeblockGet.getOrigintext();
        return true;
    }
}
