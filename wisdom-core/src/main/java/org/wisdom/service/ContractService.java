package org.wisdom.service;

public interface ContractService {

    Object getParseContractTx(String txhash);

    Object getRLPContractTx(String txhash);

    Object getParseContractAddress(byte[] addressToPubkeyHash);
}
