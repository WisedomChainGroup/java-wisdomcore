package org.wisdom.contract;

import org.wisdom.db.AccountState;

import java.util.List;

public interface AnalysisContract extends RLPBeanInterface {

    enum MethodRule {
        CHANGEOWNER, ASSETTRANSFER, INCREASED, MULTTRANSFER
    }

    List<AccountState> update(List<AccountState> accountStateList);

}
