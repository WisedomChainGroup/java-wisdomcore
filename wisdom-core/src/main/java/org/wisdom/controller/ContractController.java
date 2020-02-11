package org.wisdom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.service.ContractService;

import java.util.Arrays;
import java.util.List;

@RestController
public class ContractController {

    @Autowired
    ContractService contractService;

    @RequestMapping(method = RequestMethod.GET, value = "/ParseContractTx")
    public Object ParseContractTx(@RequestParam(value = "txhash") String txhash) {
        if (txhash.equals("") || txhash == "") {
            return APIResult.newFailed("The transaction hash cannot be null");
        }
        return contractService.getParseContractTx(txhash.trim());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/RLPContractTx")
    public Object RLPContractTx(@RequestParam(value = "txhash") String txhash) {
        if (txhash.equals("") || txhash == "") {
            return APIResult.newFailed("The transaction hash cannot be null");
        }
        return contractService.getRLPContractTx(txhash.trim());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/TokenBalance")
    public Object TokenBalance(@RequestParam(value = "address") String address, @RequestParam(value = "code") String code) {
        if (KeystoreAction.verifyAddress(address) != 0) {
            return APIResult.newFailed("Invalid address");
        }
        return contractService.getAssetBalanceObject(code, KeystoreAction.addressToPubkeyHash(address));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ParseAssetAddress")
    public Object ParseAssetAddress(@RequestParam(value = "address") String address) {
        if (KeystoreAction.verifyAddress(address) != 0) {
            return APIResult.newFailed("Invalid address");
        }
        return contractService.getParseAssetAddress(KeystoreAction.addressToPubkeyHash(address));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/AddressType")
    public Object AddressType(@RequestParam(value = "address") String address){
        if (KeystoreAction.verifyAddress(address) != 0) {
            return APIResult.newFailed("Address format error");
        }
        return contractService.AddressType(address);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/TokenListBalance")
    public Object TokenListBalance (@RequestParam(value = "address") String address, @RequestParam(value = "codes") String codes) {
        if (KeystoreAction.verifyAddress(address) != 0) {
            return APIResult.newFailed("Invalid address");
        }
        List<String> codeList = Arrays.asList(codes.split(","));
        if (codeList.size() == 0){
            return APIResult.newFailed("Codelist Can not be null");
        }
        return contractService.getTokenListBalance(KeystoreAction.addressToPubkeyHash(address),codeList);
    }
}
