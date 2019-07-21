package org.wisdom.Controller;

import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.core.TransactionPool;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.util.Arrays;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    TransactionPool transactionPool;

    @RequestMapping(value="/getTrantool",method = RequestMethod.GET)
    public Object getTrantool(@RequestParam("address") String address){
        List<Transaction> list=transactionPool.getAll();
        byte[] pubkeyhash=KeystoreAction.addressToPubkeyHash(address);
        JSONArray jsonArray = new JSONArray();
        for(Transaction t:list){
            if(Arrays.equals(pubkeyhash,t.to)){
                JSONObject json = new JSONObject();
                json.put("tranhaxh",Hex.encodeHexString(t.getHash()));
                json.put("type",t.type);
                json.put("nonce",t.nonce);
                json.put("from", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(t.from))));
                json.put("amount",t.amount);
                json.put("to",Hex.encodeHexString(t.to));
                jsonArray.add(json);
            }
        }
        return jsonArray;
    }
}
