package org.wisdom.service.Impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.MultipleDefinition.Multiple;
import org.wisdom.core.Block;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.service.ContractService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    WisdomRepository wisdomRepository;

    @Override
    public Object getParseContractTx(String txhash) {
        try {
            Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(RipemdUtility.ripemd160(Hex.decodeHex(txhash.toCharArray())));
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
            Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(RipemdUtility.ripemd160(Hex.decodeHex(txhash.toCharArray())));
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

    @Deprecated
    @Override
    public Object getTokenBalance(byte[] pubhash, String code) {
        Optional<AccountState> o = wisdomRepository.getConfirmedAccountState(pubhash)
                .filter(a -> a.getType() == 1);

        if (!o.isPresent())
            return APIResult.newFailed("Account does not exist or other type of account");

        return APIResult.newSuccess(
                o.map(AccountState::getTokensMap)
                        .map(m -> m.get(pubhash))
                        .orElse(0L)
        );
    }

    @Override
    public long getAssetBalance(final String assetCode, byte[] publicKeyHash) {
        Block best = wisdomRepository.getBestBlock();
        AssetCodeInfo info =
                wisdomRepository.getAssetCodeAt(best.getHash(), assetCode.getBytes(StandardCharsets.UTF_8))
                        .orElseThrow(() -> new RuntimeException("asset " + assetCode + " not found"));

        AccountState asset =
                wisdomRepository.getConfirmedAccountState(publicKeyHash)
                        .filter(a -> a.getType() == 0)
                        .orElseThrow(() -> new RuntimeException("Account does not exist"));

        return asset.getTokensMap().getOrDefault(info.getAsset160hash(), 0L);
    }

    @Override
    public Object AddressType(String address) {
        Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(KeystoreAction.addressToPubkeyHash(address));
        if(!accountState.isPresent())return APIResult.newFailed("Inactive address");
        if(accountState.get().getType() == 0){//普通
            return APIResult.newSuccess("0");
        }else if (accountState.get().getType() == 2){//多签
            return APIResult.newSuccess("2");
        }
        return APIResult.newFailed("Invalid address");
    }

    @Override
    public Object getTokenListBalance(byte[] pubkeyHash, List<String> codeList) {
        JSONObject codeJson = new JSONObject();
        byte[] wdcByte = new byte[20];
        for (String code : codeList){
            Long balance;
            if (code.equals(Hex.encodeHexString(wdcByte))){
                Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(pubkeyHash);
                if(!accountState.isPresent())return APIResult.newFailed("Inactive address");
                balance = accountState.get().getAccount().getBalance();
            }else{
                balance = getAssetBalance(code, pubkeyHash);
            }
            codeJson.put(code,balance);
        }
        return APIResult.newSuccess(codeJson);
    }
}
