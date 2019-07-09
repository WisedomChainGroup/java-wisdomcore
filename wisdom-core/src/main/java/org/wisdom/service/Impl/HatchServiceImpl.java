package org.wisdom.service.Impl;

import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.service.HatchService;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HatchServiceImpl implements HatchService {

    @Autowired
    AccountDB accountDB;

    @Override
    public long getBalance(String pubkeyhash) {
        try {
            byte[] pubkey= Hex.decodeHex(pubkeyhash.toCharArray());
            long balance=accountDB.getBalance(pubkey);
            return balance;
        } catch (DecoderException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getNonce(String pubkeyhash) {
        try {
            byte[] pubkey= Hex.decodeHex(pubkeyhash.toCharArray());
            long nonce=accountDB.getNonce(pubkey);
            return nonce;
        } catch (DecoderException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getTransfer(int height) {
        List<Map<String,Object>> list=accountDB.selectlistTran(height,1, Transaction.GAS_TABLE[1]);
        JSONArray jsonArray = new JSONArray();
        for(Map<String,Object> map:list){
            JSONObject json = JSONObject.fromObject( map );
            byte[] from=json.getString("fromAddress").getBytes();
            byte[] to=json.getString("coinAddress").getBytes();
            json.put("fromAddress",KeystoreAction.pubkeyHashToAddress(RipemdUtility.ripemd160(SHA3Utility.keccak256(from)),(byte)0x00));
            json.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
            jsonArray.add(json);
        }
        return APIResult.newFailResult(1,"SUCCESS",jsonArray);
    }

    @Override
    public Object getHatch(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistHacth(height,9);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                JSONObject json = JSONObject.fromObject( map );
                byte[] to=json.getString("coinAddress").getBytes();
                String payload=json.getString("payload");
                byte[] payloadbyte=Hex.decodeHex(payload);
                HatchModel.Payload payloadproto=HatchModel.Payload.parseFrom(payloadbyte);
                int days=payloadproto.getType();
                String sharpubkeyhex=payloadproto.getSharePubkeyHash();
                String sharpubkey="";
                if(sharpubkeyhex!=null && sharpubkeyhex!=""){
                    byte[] sharepubkeyhash=Hex.decodeHex(sharpubkey.toCharArray());
                    sharpubkey=KeystoreAction.pubkeyHashToAddress(sharepubkeyhash,(byte)0x00);
                }
                json.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
                json.put("blockType",days);
                json.put("inviteAddress",sharpubkey);
                json.remove("payload");
                jsonArray.add(json);
            }
            return APIResult.newFailResult(1,"SUCCESS",jsonArray);
        }catch (Exception e){
            e.printStackTrace();
            return APIResult.newFailResult(-1,"Data acquisition error");
        }
    }

    @Override
    public Object getInterest(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistInterest(height,10);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                JSONObject json = JSONObject.fromObject( map );
                byte[] to=json.getString("coinAddress").getBytes();
                json.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
                jsonArray.add(json);
            }
            return APIResult.newFailResult(1,"SUCCESS",jsonArray);
        }catch (Exception e){
            e.printStackTrace();
            return APIResult.newFailResult(-1,"Data acquisition error");
        }
    }

    @Override
    public Object getShare(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistShare(height,11);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                JSONObject json = JSONObject.fromObject( map );
                byte[] to=json.getString("coinAddress").getBytes();
                byte[] invite=json.getString("inviteAddress").getBytes();
                json.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
                json.put("inviteAddress",KeystoreAction.pubkeyHashToAddress(invite,(byte)0x00));
                jsonArray.add(json);
            }
            return APIResult.newFailResult(1,"SUCCESS",jsonArray);
        }catch (Exception e){
            e.printStackTrace();
            return APIResult.newFailResult(-1,"Data acquisition error");
        }
    }
}
