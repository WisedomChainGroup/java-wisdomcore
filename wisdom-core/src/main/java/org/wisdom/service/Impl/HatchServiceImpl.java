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
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.dao.TransactionDaoJoined;
import org.wisdom.db.AccountState;
import org.wisdom.db.SyncTransactionCustomize;
import org.wisdom.db.WisdomRepository;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.service.HatchService;
import org.wisdom.core.account.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class HatchServiceImpl implements HatchService {

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    RateTable rateTable;

    @Autowired
    Configuration configuration;

    @Autowired
    private WisdomRepository repository;

    @Autowired
    SyncTransactionCustomize syncTransactionCustomize;

    @Autowired
    TransactionDaoJoined transDaoJoined;

    @Override
    public Object getBalance(String pubkeyhash) {
        try {
            byte[] pubkeyhashbyte = Hex.decodeHex(pubkeyhash.toCharArray());
            Optional<AccountState> ao = repository.getConfirmedAccountState(pubkeyhashbyte);
            if (!ao.isPresent()) {
                return APIResult.newFailResult(2000, "SUCCESS", 0);
            }
            long balance = ao.get().getAccount().getBalance();
            return APIResult.newFailResult(2000, "SUCCESS", balance);
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getNonce(String pubkeyhash) {
        try {
            byte[] pubkey = Hex.decodeHex(pubkeyhash.toCharArray());
            Block block = repository.getBestBlock();
            Optional<AccountState> accountState = repository.getAccountStateAt(block.getHash(), pubkey);
            if (!accountState.isPresent()) {
                return APIResult.newFailResult(2000, "SUCCESS",0);
            }
            long nonce = accountState.get().getAccount().getNonce();
            return APIResult.newFailResult(2000, "SUCCESS", nonce);
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getTransfer(long height) {
        List<Map<String, Object>> list = transDaoJoined.getTransferByHeightAndTypeAndGas(height, 1, Transaction.GAS_TABLE[1]);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> map : list) {
            byte[] tranHash = (byte[]) map.get("tranHash");
            byte[] from = (byte[]) map.get("fromAddress");
            byte[] to = (byte[]) map.get("coinAddress");
            map.put("tranHash", Hex.encodeHexString(tranHash));
            map.put("fromAddress", KeystoreAction.pubkeyHashToAddress(RipemdUtility.ripemd160(SHA3Utility.keccak256(from)), (byte) 0x00, ""));
            map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(to, (byte) 0x00, ""));
            jsonArray.add(map);
        }
        return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
    }

    @Override
    public Object getHatch(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getHatchByHeightAndType(height, 9);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] tranHash = (byte[]) map.get("coinHash");
                byte[] to = (byte[]) map.get("coinAddress");
                byte[] payload = (byte[]) map.get("payload");
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(payload);
                int days = payloadproto.getType();
                String sharpubkeyhex = payloadproto.getSharePubkeyHash();
                String sharpubkey = "";
                if (sharpubkeyhex != null && sharpubkeyhex != "") {
                    byte[] sharepubkeyhash = Hex.decodeHex(sharpubkeyhex.toCharArray());
                    sharpubkey = KeystoreAction.pubkeyHashToAddress(sharepubkeyhash, (byte) 0x00, "");
                }
                map.put("coinHash", Hex.encodeHexString(tranHash));
                map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(to, (byte) 0x00, ""));
                map.put("blockType", days);
                map.put("inviteAddress", sharpubkey);
                map.remove("payload");
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "ERROR");
        }
    }

    @Override
    public Object getInterest(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getInterestByHeightAndType(height, 10);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] tranHash = (byte[]) map.get("tranHash");
                map.put("tranHash", Hex.encodeHexString(tranHash));
                byte[] to = (byte[]) map.get("coinAddress");
                map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(to, (byte) 0x00, ""));
                //分享者
                byte[] coinHash = (byte[]) map.get("coinHash");
                Transaction transaction = wisdomBlockChain.getTransaction(coinHash);
                if (transaction == null) {
                    return APIResult.newFailResult(5000, "Hatching transactions do not exist");
                }
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(transaction.payload);
                String sharpub = payloadproto.getSharePubkeyHash();
                if (sharpub != null && !sharpub.equals("")) {
                    map.put("inviteAddress", KeystoreAction.pubkeyHashToAddress(Hex.decodeHex(sharpub.toCharArray()), (byte) 0x00, ""));
                } else {
                    map.put("inviteAddress", "");
                }
                map.put("coinHash", Hex.encodeHexString(coinHash));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getShare(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getShareByHeightAndType(height, 11);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] coinHash = (byte[]) map.get("coinHash");
                byte[] tranHash = (byte[]) map.get("tranHash");
                byte[] to = (byte[]) map.get("coinAddress");
                byte[] invite = (byte[]) map.get("inviteAddress");
                map.put("coinHash", Hex.encodeHexString(coinHash));
                map.put("tranHash", Hex.encodeHexString(tranHash));
                map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(to, (byte) 0x00, ""));
                map.put("inviteAddress", KeystoreAction.pubkeyHashToAddress(invite, (byte) 0x00, ""));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getCost(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getCostByHeightAndType(height, 12);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] coinAddress = (byte[]) map.get("coinAddress");
                byte[] tranHash = (byte[]) map.get("tranHash");
                byte[] tradeHash = (byte[]) map.get("tradeHash");
                map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(coinAddress, (byte) 0x00, ""));
                map.put("tranHash", Hex.encodeHexString(tranHash));
                map.put("tradeHash", Hex.encodeHexString(tradeHash));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getVote(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getVoteByHeightAndType(height, 2);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] toAddress = (byte[]) map.get("toAddress");
                byte[] coinAddress = (byte[]) map.get("coinAddress");
                byte[] coinHash = (byte[]) map.get("coinHash");
                map.put("toAddress", KeystoreAction.pubkeyHashToAddress(toAddress, (byte) 0x00, ""));
                map.put("coinAddress", KeystoreAction.pubkeyToAddress(coinAddress, (byte) 0x00, ""));
                map.put("coinHash", Hex.encodeHexString(coinHash));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getCancelVote(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getCancelVoteByHeightAndType(height, 13);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] toAddress = (byte[]) map.get("toAddress");
                byte[] coinAddress = (byte[]) map.get("coinAddress");
                byte[] coinHash = (byte[]) map.get("coinHash");
                byte[] tradeHash = (byte[]) map.get("tradeHash");
                map.put("toAddress", KeystoreAction.pubkeyHashToAddress(toAddress, (byte) 0x00, ""));
                map.put("coinAddress", KeystoreAction.pubkeyToAddress(coinAddress, (byte) 0x00, ""));
                map.put("coinHash", Hex.encodeHexString(coinHash));
                map.put("tradeHash", Hex.encodeHexString(tradeHash));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getMortgage(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getMortgageByHeightAndType(height, 14);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] coinAddress = (byte[]) map.get("coinAddress");
                byte[] coinHash = (byte[]) map.get("coinHash");
                map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(coinAddress, (byte) 0x00, ""));
                map.put("coinHash", Hex.encodeHexString(coinHash));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getCancelMortgage(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getCancelMortgageByHeightAndType(height, 15);
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : list) {
                byte[] toAddress = (byte[]) map.get("coinAddress");
                byte[] coinHash = (byte[]) map.get("coinHash");
                byte[] tradeHash = (byte[]) map.get("tradeHash");
                map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(toAddress, (byte) 0x00, ""));
                map.put("coinHash", Hex.encodeHexString(coinHash));
                map.put("tradeHash", Hex.encodeHexString(tradeHash));
                jsonArray.add(map);
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonArray);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getNowInterest(String tranhash) {
        try {
            byte[] trhash = Hex.decodeHex(tranhash.toCharArray());
            //孵化事务
            Transaction transaction = wisdomBlockChain.getTransaction(trhash);
            if (transaction == null) {
                return APIResult.newFailResult(5000, "Transaction unavailable. Check transaction hash");
            }
            Optional<AccountState> oa = repository.getConfirmedAccountState(transaction.to);
            if (!oa.isPresent()) {
                return APIResult.newFailResult(5000, "The account does not exist");
            }
            Map<byte[], Incubator> interestMap = oa.get().getInterestMap();
            if (!interestMap.containsKey(trhash)) {
                return APIResult.newFailResult(5000, "Error in incubation state acquisition");
            }
            //查询当前孵化记录
            Incubator incubator = interestMap.get(trhash);
            if (incubator.getInterest_amount() == 0 || incubator.getCost() == 0) {
                return APIResult.newFailResult(3000, "There is no interest to be paid");
            }
            HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(transaction.payload);
            int days = payloadproto.getType();
            String nowrate = rateTable.selectrate(transaction.height, days);
            //当前最高高度
            long maxhieght = wisdomBlockChain.getTopHeight();
            long differheight = maxhieght - incubator.getLast_blockheight_interest();
            int differdays = (int) (differheight / configuration.getDay_count(maxhieght));
            if (differdays == 0) {
                return APIResult.newFailResult(5000, "Interest less than one day");
            }
            BigDecimal aount = new BigDecimal(transaction.amount);
            BigDecimal nowratebig = new BigDecimal(nowrate);
            BigDecimal dayrate = aount.multiply(nowratebig);
            long dayratelong = dayrate.longValue();
            JSONObject jsonObject = new JSONObject();
            //判断利息金额小于每天利息
            if (incubator.getInterest_amount() < dayrate.longValue()) {
                jsonObject.put("dueinAmount", incubator.getInterest_amount());
                jsonObject.put("capitalAmount", incubator.getInterest_amount());
            } else {
                long muls = (long) (incubator.getInterest_amount() % dayrate.longValue());
                if (muls != 0) {
                    long syamount = muls;
                    jsonObject.put("dueinAmount", syamount);
                    jsonObject.put("capitalAmount", incubator.getInterest_amount());
                } else {
                    int maxdays = (int) (incubator.getInterest_amount() / dayrate.longValue());
                    long lastdays = 0;
                    if (maxdays > differdays) {
                        lastdays = differdays;
                    } else {
                        lastdays = maxdays;
                    }
                    //当前可获取利息
                    BigDecimal lastdaysbig = BigDecimal.valueOf(lastdays);
                    BigDecimal dayrtebig = BigDecimal.valueOf(dayratelong);
                    long interset = dayrtebig.multiply(lastdaysbig).longValue();
                    jsonObject.put("dueinAmount", interset);
                    jsonObject.put("capitalAmount", incubator.getInterest_amount());
                }
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonObject);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getNowShare(String tranhash) {
        try {
            byte[] trhash = Hex.decodeHex(tranhash.toCharArray());
            //孵化事务
            Transaction transaction = wisdomBlockChain.getTransaction(trhash);
            if (transaction == null) {
                return APIResult.newFailResult(5000, "Transaction unavailable. Check transaction hash");
            }
            HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(transaction.payload);
            Optional<AccountState> oa = repository.getConfirmedAccountState(Hex.decodeHex(payloadproto.getSharePubkeyHash().toCharArray()));
            if (!oa.isPresent()) {
                return APIResult.newFailResult(5000, "The account does not exist");
            }
            Map<byte[], Incubator> shareMap = oa.get().getShareMap();
            if (!shareMap.containsKey(trhash)) {
                return APIResult.newFailResult(5000, "Error in incubation state acquisition");
            }
            //查询当前孵化记录
            Incubator incubator = shareMap.get(trhash);
            if (incubator == null) {
                return APIResult.newFailResult(5000, "Error in incubation state acquisition");
            }
            if (incubator.getShare_amount() == 0) {
                return APIResult.newFailResult(3000, "There is no share to be paid");
            }
            int days = payloadproto.getType();
            String nowrate = rateTable.selectrate(transaction.height, days);
            //当前最高高度
            long maxhieght = wisdomBlockChain.getTopHeight();
            long differheight = maxhieght - incubator.getLast_blockheight_share();
            int differdays = (int) (differheight / configuration.getDay_count(maxhieght));
            if (differdays == 0) {
                return APIResult.newFailResult(5000, "Interest less than one day");
            }
            BigDecimal aount = new BigDecimal(transaction.amount);
            BigDecimal nowratebig = new BigDecimal(nowrate);
            BigDecimal lv = BigDecimal.valueOf(0.1);
            BigDecimal nowlv = aount.multiply(nowratebig);
            BigDecimal dayrate = nowlv.multiply(lv);
            long dayrates = dayrate.longValue();
            JSONObject jsonObject = new JSONObject();
            //判断分享金额小于每天可提取的
            if (incubator.getShare_amount() < dayrate.longValue()) {
                jsonObject.put("dueinAmount", incubator.getShare_amount());
                jsonObject.put("capitalAmount", incubator.getShare_amount());
            } else {
                long muls = (long) (incubator.getShare_amount() % dayrate.longValue());
                if (muls != 0) {
                    long syamount = muls;
                    jsonObject.put("dueinAmount", syamount);
                    jsonObject.put("capitalAmount", incubator.getShare_amount());
                } else {
                    int maxdays = (int) (incubator.getShare_amount() / dayrate.longValue());
                    long lastdays = 0;
                    if (maxdays > differdays) {
                        lastdays = differdays;
                    } else {
                        lastdays = maxdays;
                    }
                    //当前可获取分享
                    BigDecimal lastdaysbig = BigDecimal.valueOf(lastdays);
                    BigDecimal dayratesbig = BigDecimal.valueOf(dayrates);
                    long share = dayratesbig.multiply(lastdaysbig).longValue();
                    jsonObject.put("dueinAmount", share);
                    jsonObject.put("capitalAmount", incubator.getShare_amount());
                }
            }
            return APIResult.newFailResult(2000, "SUCCESS", jsonObject);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    @Deprecated
    public Object getTxrecordFromAddress(String address) {
        try {
            if (KeystoreAction.verifyAddress(address) == 0) {
                byte[] pubkeyhash = KeystoreAction.addressToPubkeyHash(address);
                List<Map<String, Object>> list = new ArrayList<>();
                List<Map<String, Object>> tolist = syncTransactionCustomize.selectTranto(pubkeyhash);
                for (Map<String, Object> to : tolist) {
                    Map<String, Object> maps = to;
                    String from = maps.get("from").toString();
                    String fromaddress = KeystoreAction.pubkeyToAddress(Hex.decodeHex(from.toCharArray()), (byte) 0x00, "");
                    maps.put("from", fromaddress);
                    String topubkeyhash = maps.get("to").toString();
                    String toaddress = KeystoreAction.pubkeyHashToAddress(Hex.decodeHex(topubkeyhash.toCharArray()), (byte) 0x00, "");
                    maps.put("to", toaddress);
                    long time = Long.valueOf(maps.get("datetime").toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dates = new Date(time * 1000);
                    String date = sdf.format(dates);
                    maps.put("datetime", date);
                    maps.put("type", "+");
                    list.add(maps);
                }
                List<Map<String, Object>> fromlist = syncTransactionCustomize.selectTranfrom(pubkeyhash);
                for (Map<String, Object> from : fromlist) {
                    Map<String, Object> maps = from;
                    String froms = maps.get("from").toString();
                    byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(Hex.decodeHex(froms.toCharArray())));
                    if (Arrays.equals(frompubhash, pubkeyhash)) {
                        String fromaddress = KeystoreAction.pubkeyHashToAddress(frompubhash, (byte) 0x00, "");
                        maps.put("from", fromaddress);
                        String topubkeyhash = maps.get("to").toString();
                        String toaddress = KeystoreAction.pubkeyHashToAddress(Hex.decodeHex(topubkeyhash.toCharArray()), (byte) 0x00, "");
                        maps.put("to", toaddress);
                        long time = Long.valueOf(maps.get("datetime").toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dates = new Date(time * 1000);
                        String date = sdf.format(dates);
                        maps.put("datetime", date);
                        maps.put("type", "-");
                        list.add(maps);
                    }
                }
                list = list.stream().sorted((p1, p2) -> Integer.valueOf(p1.get("height").toString()) - Integer.valueOf(p2.get("height").toString()))
                        .collect(toList());
                return APIResult.newFailResult(2000, "SUCCESS", list);
            } else {
                return APIResult.newFailResult(5000, "Address check error");
            }
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

    @Override
    public Object getCoinBaseList(long height) {
        try {
            List<Map<String, Object>> list = transDaoJoined.getCoinBaseByHeightAndType(height);
            int count = 0;
            Map<String, Object> maps = new HashMap<>();
            for (Map<String, Object> map : list) {
                if (Integer.valueOf(map.get("type").toString()) == 0) {
                    byte[] toAddress = (byte[]) map.get("coinAddress");
                    byte[] coinHash = (byte[]) map.get("coinHash");
                    map.put("coinAddress", KeystoreAction.pubkeyHashToAddress(toAddress, (byte) 0x00, ""));
                    map.put("coinHash", Hex.encodeHexString(coinHash));
                    maps.putAll(map);
                    break;
                }
                count++;
            }
            maps.put("trancount", count);
            return APIResult.newFailResult(2000, "SUCCESS", maps);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "Exception error");
        }
    }
}