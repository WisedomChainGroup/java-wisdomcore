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
public class HashtimeblockTransfer implements AnalysisContract {
    @RLP(0)
    private Long value;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashtimeblockTransfer.builder().value(this.getValue()));
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashtimeblockTransfer hashtimeblockTransfer = RLPCodec.decode(payload,HashtimeblockTransfer.class);
        if(hashtimeblockTransfer == null){
            return false;
        }
        this.value = hashtimeblockTransfer.getValue();
        return true;
    }
}
