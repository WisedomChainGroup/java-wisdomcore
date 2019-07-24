/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.service.Impl;

import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Configuration;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.service.HatchService;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HatchServiceImpl implements HatchService {

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    RateTable rateTable;

    @Autowired
    Configuration configuration;

    @Override
    public Object getBalance(String pubkeyhash) {
        try {
            byte[] pubkey= Hex.decodeHex(pubkeyhash.toCharArray());
            long balance=accountDB.getBalance(pubkey);
            return APIResult.newFailResult(2000,"SUCCESS",balance);
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @Override
    public Object getNonce(String pubkeyhash) {
        try {
            byte[] pubkey= Hex.decodeHex(pubkeyhash.toCharArray());
            long nonce=accountDB.getNonce(pubkey);
            return APIResult.newFailResult(2000,"SUCCESS",nonce);
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @Override
    public Object getTransfer(int height) {
        List<Map<String,Object>> list=accountDB.selectlistTran(height,1, Transaction.GAS_TABLE[1]);
        JSONArray jsonArray = new JSONArray();
        for(Map<String,Object> map:list){
            byte[] from= (byte[]) map.get("fromAddress");
            byte[] to= (byte[]) map.get("coinAddress");
            map.put("fromAddress",KeystoreAction.pubkeyHashToAddress(RipemdUtility.ripemd160(SHA3Utility.keccak256(from)),(byte)0x00));
            map.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
            jsonArray.add(map);
        }
        return APIResult.newFailResult(2000,"SUCCESS",jsonArray);
    }

    @Override
    public Object getHatch(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistHacth(height,9);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                JSONObject json = JSONObject.fromObject( map );
                String to=json.getString("coinAddress");
                byte[] tohash=Hex.decodeHex(to.toCharArray());
                String payload=json.getString("payload");
                byte[] payloadbyte=Hex.decodeHex(payload);
                HatchModel.Payload payloadproto=HatchModel.Payload.parseFrom(payloadbyte);
                int days=payloadproto.getType();
                String sharpubkeyhex=payloadproto.getSharePubkeyHash();
                String sharpubkey="";
                if(sharpubkeyhex!=null && sharpubkeyhex!=""){
                    byte[] sharepubkeyhash=Hex.decodeHex(sharpubkeyhex.toCharArray());
                    sharpubkey=KeystoreAction.pubkeyHashToAddress(sharepubkeyhash,(byte)0x00);
                }
                json.put("coinAddress",KeystoreAction.pubkeyHashToAddress(tohash,(byte)0x00));
                json.put("blockType",days);
                json.put("inviteAddress",sharpubkey);
                json.remove("payload");
                jsonArray.add(json);
            }
            return APIResult.newFailResult(2000,"SUCCESS",jsonArray);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"ERROR");
        }
    }

    @Override
    public Object getInterest(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistInterest(height,10);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                byte[] to= (byte[]) map.get("coinAddress");
                map.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
                //分享者
                String tranhex=map.get("coinHash").toString();
                byte[] tranhash=Hex.decodeHex(tranhex.toCharArray());
                Incubator incubator=incubatorDB.selectIncubator(tranhash);
                if(incubator!=null){
                    byte[] share=incubator.getShare_pubkeyhash();
                    if(share!=null && share.length>0){
                        map.put("inviteAddress",KeystoreAction.pubkeyHashToAddress(share,(byte)0x00));
                    }else{
                        map.put("inviteAddress","");
                    }
                }else{
                    return APIResult.newFailResult(5000,"Error in incubation state acquisition");
                }
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000,"SUCCESS",jsonArray);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @Override
    public Object getShare(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistShare(height,11);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                byte[] to= (byte[]) map.get("coinAddress");
                byte[] invite= (byte[]) map.get("inviteAddress");
                map.put("coinAddress",KeystoreAction.pubkeyHashToAddress(to,(byte)0x00));
                map.put("inviteAddress",KeystoreAction.pubkeyHashToAddress(invite,(byte)0x00));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000,"SUCCESS",jsonArray);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @Override
    public Object getCost(int height) {
        try{
            List<Map<String,Object>> list=accountDB.selectlistCost(height,12);
            JSONArray jsonArray = new JSONArray();
            for(Map<String,Object> map:list){
                String coinAddress=map.get("coinAddress").toString();
                byte[] pubkeyhash=Hex.decodeHex(coinAddress.toCharArray());
                map.put("coinAddress",KeystoreAction.pubkeyHashToAddress(pubkeyhash,(byte)0x00));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000,"SUCCESS",jsonArray);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @Override
    public Object getNowInterest(String tranhash) {
        try{
            byte[] trhash= Hex.decodeHex(tranhash.toCharArray());
            //查询当前孵化记录
            Incubator incubator=incubatorDB.selectIncubator(trhash);
            if(incubator==null){
                return APIResult.newFailResult(5000,"Error in incubation state acquisition");
            }
            if(incubator.getInterest_amount()==0 || incubator.getCost()==0){
                return APIResult.newFailResult(5000,"There is no interest to be paid");
            }
            //孵化事务
            Transaction transaction=wisdomBlockChain.getTransaction(trhash);
            if(transaction==null){
                return APIResult.newFailResult(5000,"Transaction unavailable. Check transaction hash");
            }
            HatchModel.Payload payloadproto=HatchModel.Payload.parseFrom(transaction.payload);
            int days=payloadproto.getType();
            double nowrate=rateTable.selectrate(transaction.height,days);
            //当前最高高度
            long maxhieght=wisdomBlockChain.getCurrentTotalWeight();
            long differheight=maxhieght-incubator.getLast_blockheight_interest();
            int differdays=(int)(differheight/configuration.getDay_count());
            if(differdays==0){
                return APIResult.newFailResult(5000,"Interest less than one day");
            }
            long dayrate=(long)(transaction.amount*nowrate);
            int maxdays=(int)(incubator.getInterest_amount()/dayrate);
            long lastdays=0;
            if(maxdays>differdays){
                lastdays=differdays;
            }else{
                lastdays=maxdays;
            }
            //当前可获取利息
            long interset=dayrate*lastdays;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("dueinAmount",interset);
            jsonObject.put("capitalAmount",incubator.getInterest_amount());
            return APIResult.newFailResult(2000,"SUCCESS",jsonObject);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @Override
    public Object getTxrecordFromAddress(String address) {
        try {
            if(KeystoreAction.verifyAddress(address)==0){
                byte[] pubkeyhash=KeystoreAction.addressToPubkeyHash(address);
                List<Map<String,Object>> list=new ArrayList<>();
                List<Map<String,Object>> tolist=accountDB.selectTranto(pubkeyhash);
                for(Map<String,Object> to:tolist){
                    Map<String,Object> maps=to;
                    String from=maps.get("from").toString();
                    String fromaddress=KeystoreAction.pubkeyToAddress(Hex.decodeHex(from.toCharArray()),(byte)0x00);
                    maps.put("from",fromaddress);
                    String topubkeyhash=maps.get("to").toString();
                    String toaddress=KeystoreAction.pubkeyHashToAddress(Hex.decodeHex(topubkeyhash.toCharArray()),(byte)0x00);
                    maps.put("to",toaddress);
                    long time= Long.valueOf(maps.get("datetime").toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dates = new Date(time*1000);
                    String date=sdf.format(dates);
                    maps.put("datetime",date);
                    maps.put("type","+");
                    list.add(maps);
                }
                List<Map<String,Object>> fromlist=accountDB.selectTranfrom(pubkeyhash);
                for(Map<String,Object> from:fromlist){
                    Map<String,Object> maps=from;
                    String froms=maps.get("from").toString();
                    byte[] frompubhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(Hex.decodeHex(froms.toCharArray())));
                    if(Arrays.equals(frompubhash,pubkeyhash)){
                        String fromaddress=KeystoreAction.pubkeyHashToAddress(frompubhash,(byte)0x00);
                        maps.put("from",fromaddress);
                        String topubkeyhash=maps.get("to").toString();
                        String toaddress=KeystoreAction.pubkeyHashToAddress(Hex.decodeHex(topubkeyhash.toCharArray()),(byte)0x00);
                        maps.put("to",toaddress);
                        long time= Long.valueOf(maps.get("datetime").toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dates = new Date(time*1000);
                        String date=sdf.format(dates);
                        maps.put("datetime",date);
                        maps.put("type","-");
                        list.add(maps);
                    }
                }
                Collections.sort(list, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        Integer name1 = Integer.valueOf(o1.get("height").toString()) ;
                        Integer name2 = Integer.valueOf(o2.get("height").toString()) ;
                        return name2.compareTo(name1);
                    }
                });
                return APIResult.newFailResult(2000,"SUCCESS",list);
            }else{
                return APIResult.newFailResult(5000,"Address check error");
            }
        } catch (DecoderException e) {
            e.printStackTrace();
            return APIResult.newFailResult(5000,"Exception error");
        }
    }
}