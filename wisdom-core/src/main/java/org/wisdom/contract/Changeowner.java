package org.wisdom.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Changeowner implements AnalysisContract{

    private byte[] newowner;

    @Override
    public APIResult FormatCheck(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        try{

        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return new byte[0];
    }
}
