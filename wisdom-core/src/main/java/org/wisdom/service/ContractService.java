package org.wisdom.service;

public interface ContractService {

    Object getParseContractTx(String txhash);

    Object getRLPContractTx(String txhash);

    Object getParseAssetAddress(byte[] addressToPubkeyHash);

    Object getTokenBalance(byte[] addressToPubkeyHash, String code);
}
