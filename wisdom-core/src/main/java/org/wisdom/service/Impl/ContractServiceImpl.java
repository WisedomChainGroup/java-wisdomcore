package org.wisdom.service.Impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.service.ContractService;
import org.wisdom.util.ByteUtil;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Override
    public Object getParseContractTx(String txhash) {
        try {
            Transaction transaction = wisdomBlockChain.getTransaction(Hex.decodeHex(txhash.toCharArray()));
            if (transaction == null || transaction.type != Transaction.Type.DEPLOY_CONTRACT.ordinal()) {
                return APIResult.newFailed("The thing hash does not exist or is not a contract transaction");
            }
            byte[] payload = transaction.payload;
            if (payload[0] == 0) {//代币
                return APIResult.newSuccess(Asset.getAsset(ByteUtil.bytearrayridfirst(payload)));
            } else {
                return APIResult.newFailed("Other contracts are closed");
            }
        } catch (DecoderException e) {
            return APIResult.newFailed("Transaction hash resolution error");
        }
    }

    @Override
    public Object getRLPContractTx(String txhash) {
        try {
            Transaction transaction = wisdomBlockChain.getTransaction(Hex.decodeHex(txhash.toCharArray()));
            if (transaction == null || (transaction.type != Transaction.Type.CALL_CONTRACT.ordinal() && transaction.type != Transaction.Type.DEPLOY_CONTRACT.ordinal())) {
                return APIResult.newFailed("The thing hash does not exist or is not a contract transaction");
            }
            return APIResult.newSuccess(ByteUtil.bytearrayridfirst(transaction.payload));
        } catch (DecoderException e) {
            return APIResult.newFailed("Transaction hash resolution error");
        }
    }

    @Override
    public Object getParseContractAddress(byte[] addressToPubkeyHash) {
        return null;
    }
}
