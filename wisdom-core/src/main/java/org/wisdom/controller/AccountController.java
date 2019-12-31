package org.wisdom.controller;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.db.AccountStateTrie;
import org.wisdom.util.Address;


@RestController
public class AccountController {

    @Autowired
    private AccountStateTrie accountStateTrie;

    @RequestMapping(method = RequestMethod.GET, value = "/internal/accountState")
    public Object getAccount(@RequestParam("blockHash") String blockHash, @RequestParam("publicKeyHash") String publicKeyHash) {
        return accountStateTrie.get(Hex.decode(blockHash), Hex.decode(publicKeyHash)).get();
    }

    public static void main(String[] args) {
        System.out.println(Hex.toHexString(Address.addressToPublicKeyHash("1PpBHEx782C4VrtnQcJRTogn5UYmzCWAPH")));
    }

}
