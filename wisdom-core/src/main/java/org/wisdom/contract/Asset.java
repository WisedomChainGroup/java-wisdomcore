package org.wisdom.contract;

import lombok.*;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset implements AnalysisContract{

    private final static String Asset_changeowner="changeowner";
    private final static String Asset_transfer="transfer";
    private final static String Asset_increased="increased";

    private String code;

    private long offering;

    private long totalamount;

    private byte[] createuser;

    private byte[] owner;

    private int allowincrease;

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

    public Asset copy(){
        return new Asset(code,offering,totalamount,createuser,owner,allowincrease);
    }
}
