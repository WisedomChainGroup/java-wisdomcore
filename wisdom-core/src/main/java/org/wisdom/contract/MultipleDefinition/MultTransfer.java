package org.wisdom.contract.MultipleDefinition;

import org.wisdom.ApiResult.APIResult;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.db.AccountState;

import java.util.List;

public class MultTransfer implements AnalysisContract {

//    private

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
        return false;
    }

    @Override
    public byte[] RLPserialization() {
        return new byte[0];
    }
}
