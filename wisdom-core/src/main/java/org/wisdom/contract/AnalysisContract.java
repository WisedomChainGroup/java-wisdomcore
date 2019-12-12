package org.wisdom.contract;

import org.wisdom.ApiResult.APIResult;
import org.wisdom.db.AccountState;

import java.util.List;

public interface AnalysisContract extends RLPBeanInterface {
    
    List<AccountState> update(List<AccountState> accountStateList);

}
