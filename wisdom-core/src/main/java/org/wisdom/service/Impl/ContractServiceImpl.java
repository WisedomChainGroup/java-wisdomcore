package org.wisdom.service.Impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.MultipleDefinition.Multiple;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.service.ContractService;

import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    WisdomRepository wisdomRepository;

    @Override
    public Object getParseContractTx(String txhash) {
        try {
            Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(Hex.decodeHex(txhash.toCharArray()));
            if (!accountStateOptional.isPresent() || accountStateOptional.get().getType() == 0) {
                return APIResult.newFailed("The thing hash does not exist or is not a contract transaction");
            }
            AccountState accountState = accountStateOptional.get();
            byte[] RLPByte = accountState.getContract();
            if (accountState.getType() == 1) {//代币
                return APIResult.newSuccess(Asset.getConvertAsset(RLPByte));
            } else if (accountState.getType() == 2) {//多签
                return APIResult.newSuccess(Multiple.getConvertMultiple(RLPByte));
            }
        } catch (DecoderException e) {
            return APIResult.newFailed("Transaction hash resolution error");
        }
        return APIResult.newFailed("This contract type does not exist");
    }

    @Override
    public Object getRLPContractTx(String txhash) {
        try {
            Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(Hex.decodeHex(txhash.toCharArray()));
            if (!accountStateOptional.isPresent() || accountStateOptional.get().getType() == 0) {
                return APIResult.newFailed("The thing hash does not exist or is not a contract transaction");
            }
            return APIResult.newSuccess(accountStateOptional.get().getContract());
        } catch (DecoderException e) {
            return APIResult.newFailed("Transaction hash resolution error");
        }
    }

    @Override
    public Object getParseAssetAddress(byte[] pubhash) {
        Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(pubhash);
        if (!accountStateOptional.isPresent() || accountStateOptional.get().getType() != 1) {
            return APIResult.newFailed("The thing hash does not exist or is not a contract transaction");
        }
        return APIResult.newSuccess(Asset.getConvertAsset(accountStateOptional.get().getContract()));
    }

    @Override
    public Object getTokenBalance(byte[] pubhash, String code) {
        Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(pubhash);
        if(!accountStateOptional.isPresent() || accountStateOptional.get().getType()!=0 ){
            return APIResult.newFailed("Account does not exist or other type of account");
        }

        return null;
    }


}
