package org.wisdom.service;

import java.util.List;

public interface ContractService {

    Object getParseContractTx(String txhash);

    Object getRLPContractTx(String txhash);

    Object getParseAssetAddress(byte[] addressToPubkeyHash);

    @Deprecated
    Object getTokenBalance(byte[] addressToPubkeyHash, String code);

    /**
     *
     * @param assetCode 资产代号
     * @param publicKeyHash 用户公钥哈希
     * @return 用户拥有此类型资产的余额
     */
    long getAssetBalance(String assetCode, byte[] publicKeyHash);

    Object AddressType(String address);

    Object getTokenListBalance(byte[] pubkeyHash, List<String> codeList);

    Object getAssetBalanceObject(String assetCode, byte[] publicKeyHash);

    Object getParseContractTxByPubkeyhash(String pubkeyhash);

    Object getRateheightlockDepositBalanceByTxhash(String txhash,String publicKeyHash);
}
