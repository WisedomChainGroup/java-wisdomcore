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
package org.wisdom.command;

import lombok.Setter;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.AssetDefinition.AssetChangeowner;
import org.wisdom.contract.AssetDefinition.AssetIncreased;
import org.wisdom.contract.AssetDefinition.AssetTransfer;
import org.wisdom.contract.MultipleDefinition.MultTransfer;
import org.wisdom.contract.MultipleDefinition.Multiple;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.service.Impl.CommandServiceImpl;
import org.wisdom.util.ByteUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Setter
public class TransactionCheck {
    private static final Logger logger = LoggerFactory.getLogger(TransactionCheck.class);

    @Autowired
    Configuration configuration;

    @Autowired
    WisdomRepository wisdomRepository;

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    RateTable rateTable;

    public static final String WX = "WX";
    public static final String WR = "WR";

    private static final Long rate = 100000000L;
    private static final Long serviceCharge = 200000L;

    public APIResult TransactionFormatCheck(byte[] transfer) {
        APIResult apiResult = new APIResult();
        try {
            //version
            byte[] version = ByteUtil.bytearraycopy(transfer, 0, 1);
            if (version[0] != 0x01) {
                apiResult.setCode(5000);
                apiResult.setMessage("Version number error");
                return apiResult;
            }
            //sha3-256
            byte[] transha = ByteUtil.bytearraycopy(transfer, 1, 32);
            byte[] tranlast = ByteUtil.bytearraycopy(transfer, 33, transfer.length - 33);
            byte[] trannew = ByteUtil.prepend(tranlast, version[0]);
            if (!Arrays.equals(transha, SHA3Utility.keccak256(trannew))) {
                apiResult.setCode(5000);
                apiResult.setMessage("The transaction hash check error");
                return apiResult;
            }
            //type
            byte[] type = ByteUtil.bytearraycopy(tranlast, 0, 1);
            tranlast = ByteUtil.bytearraycopy(tranlast, 1, tranlast.length - 1);
            //nonce
            byte[] noncebyte = ByteUtil.bytearraycopy(tranlast, 0, 8);
            long nonce = BigEndian.decodeUint64(noncebyte);
            tranlast = ByteUtil.bytearraycopy(tranlast, 8, tranlast.length - 8);
            //frompubkey
            byte[] frompubkey = ByteUtil.bytearraycopy(tranlast, 0, 32);
            byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(frompubkey));
            tranlast = ByteUtil.bytearraycopy(tranlast, 32, tranlast.length - 32);
            //gasPrice
            byte[] gasbyte = ByteUtil.bytearraycopy(tranlast, 0, 8);
            long gasPrice = BigEndian.decodeUint64(gasbyte);
            //gas
            long gas = 0;
            if (15 >= type[0] && type[0] >= 0) {
                gas = Transaction.GAS_TABLE[type[0]];
            } else {
                apiResult.setCode(5000);
                apiResult.setMessage("Type check error");
                return apiResult;
            }
            //fee
            if ((gasPrice * gas) < Transaction.minFee) {
                apiResult.setCode(5000);
                apiResult.setMessage("Less than minimum handling charge");
                return apiResult;
            }
            tranlast = ByteUtil.bytearraycopy(tranlast, 8, tranlast.length - 8);
            //amount
            byte[] amountbyte = ByteUtil.bytearraycopy(tranlast, 0, 8);
            long amount = ByteUtil.byteArrayToLong(amountbyte);
            if (amount < 0) {
                apiResult.setCode(5000);
                apiResult.setMessage("The amount cannot be negative");
                return apiResult;
            }
            if (amount == 0 && (type[0] == Transaction.Type.VOTE.ordinal() || type[0] == Transaction.Type.MORTGAGE.ordinal())) {
                apiResult.setCode(5000);
                apiResult.setMessage("The amount cannot be zero");
                return apiResult;
            }
            if (amount != 0 && (type[0] == Transaction.Type.DEPOSIT.ordinal() ||
                    type[0] == Transaction.Type.DEPLOY_CONTRACT.ordinal() || type[0] == Transaction.Type.CALL_CONTRACT.ordinal())) {
                apiResult.setCode(5000);
                apiResult.setMessage("The amount must be zero");
                return apiResult;
            }
            tranlast = ByteUtil.bytearraycopy(tranlast, 8, tranlast.length - 8);
            //sig
            byte[] sigdate = ByteUtil.bytearraycopy(tranlast, 0, 64);
            tranlast = ByteUtil.bytearraycopy(tranlast, 64, tranlast.length - 64);
            //topubkeyhash
            byte[] topubkeyhash = ByteUtil.bytearraycopy(tranlast, 0, 20);
            if (type[0] == 0x09 || type[0] == 0x0a || type[0] == 0x0b || type[0] == 0x0c || type[0] == 0x0e || type[0] == 0x0f) {
                if (!Arrays.equals(frompubhash, topubkeyhash)) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("From and To are different");
                    return apiResult;
                }
            }
            //fromaddress
            boolean verifyfrom = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyHashToAddress(frompubhash, (byte) 0x00, "")) == 0);
            if (!verifyfrom) {
                apiResult.setCode(5000);
                apiResult.setMessage("From format check error");
                return apiResult;
            }
            //toaddress
            if (type[0] == 0x03 || type[0] == 0x07) {//存证、部署合约
                if (!Arrays.equals(new byte[20], topubkeyhash)) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The to is not empty");
                    return apiResult;
                }
            } else {
                boolean verifyto = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyHashToAddress(topubkeyhash, (byte) 0x00, "")) == 0);
                if (!verifyto) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("To format check error");
                    return apiResult;
                }
            }
            tranlast = ByteUtil.bytearraycopy(tranlast, 20, tranlast.length - 20);
            //bytelength
            byte[] date = ByteUtil.bytearraycopy(tranlast, 0, 4);
            int length = ByteUtil.byteArrayToInt(date);
            if (type[0] != 0x01 && type[0] != 0x02 && type[0] != Transaction.Type.MORTGAGE.ordinal()) {//转账、投票,抵押, 没有payload
                if (length == 0) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Payload cannot be empty");
                    return apiResult;
                }
            }
            if (length > 0) {
                tranlast = ByteUtil.bytearraycopy(tranlast, 4, tranlast.length - 4);
                byte[] Payload = ByteUtil.bytearraycopy(tranlast, 0, length);
                date = ByteUtil.merge(date, Payload);
            }
            //sigcheck
            byte[] nullsig = new byte[64];
            byte[] nosig = ByteUtil.merge(version, type, noncebyte, frompubkey, gasbyte, amountbyte, nullsig, topubkeyhash, date);
            Ed25519PublicKey ed25519PublicKey = new Ed25519PublicKey(frompubkey);
            boolean result = ed25519PublicKey.verify(nosig, sigdate);
            if (!result) {
                apiResult.setCode(5000);
                apiResult.setMessage("Signature check error");
                return apiResult;
            }
            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            Transaction transaction = Transaction.fromRPCBytes(transfer);
            apiResult.setData(transaction);
            return apiResult;
        } catch (Exception e) {
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
    }

    public APIResult TransactionVerify(Transaction transaction, Account account, Incubator incubator) {
        APIResult apiResult = new APIResult();
        try {
            //nonce
            long trannonce = transaction.nonce;
            long nownonce;
//            if (account == null) {
//                nownonce = accountDB.getNonce(frompubhash);
//            } else {
            nownonce = account.getNonce();
//            }
            if (nownonce >= trannonce) {
                apiResult.setCode(5000);
                apiResult.setMessage("Nonce is too small");
                return apiResult;
            }
            //type
            int type = transaction.type;
            //balance
            long tranbalance = transaction.amount;
            long nowbalance = account.getBalance();
            if (type == 0x01 || type == 0x09) {
                if ((tranbalance + transaction.getFee()) > nowbalance) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Not sufficient funds");
                    return apiResult;
                }
            } else if (type == 0x03 || type == 0x0a || type == 0x0b || type == 0x0c
                    || type == 0x0d || type == 0x0f || type == 0x07 || type == 0x08) {
                if (transaction.getFee() > nowbalance) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Not sufficient funds");
                    return apiResult;
                }
            } else if (type == 0x02 || type == 0x0e) {//vote、mortgage
                if ((tranbalance + transaction.getFee()) > nowbalance) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Not sufficient funds");
                    return apiResult;
                }
                //求余
                long remainder = tranbalance % EconomicModel.WDC;
                if (remainder != 0) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The amount must be an integer");
                    return apiResult;
                }
            }
            //payload
            byte[] payload = transaction.payload;
            if (payload != null) {
                return PayloadCheck(transaction, incubator);
            }
        } catch (Exception e) {
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    public APIResult PayloadCheck(Transaction transaction, Incubator incubator) {
        APIResult apiResult = new APIResult();
        byte[] payload = transaction.payload;
        int type = transaction.type;
        long amount = transaction.amount;
        byte[] from = transaction.from;
        byte[] topubkeyhash = transaction.to;
        switch (type) {
            case 0x09://孵化器
                apiResult = CheckHatch(amount, payload, topubkeyhash);
                break;
            case 0x0a:
            case 0x0b://提取利息、提取分享收益
                apiResult = CheckInterAndShare(type, amount, payload, incubator, topubkeyhash);
                break;
            case 0x03://存证
                apiResult = CheckDeposit(payload);
                break;
            case 0x07://部署合约
                apiResult = CheckDeployContract(payload, from, amount, topubkeyhash);
                break;
            case 0x08://调用合约
                apiResult = CheckCallContract(transaction);
                break;
            case 0x0c://提取本金
                apiResult = CheckCost(amount, payload, incubator, topubkeyhash);
                break;
            case 0x0d://撤回投票
                apiResult = CheckRecallVote(amount, payload, from, topubkeyhash);
                break;
            case 0x0f://撤回抵押
                apiResult = CheckRecallMortgage(amount, payload, topubkeyhash);
                break;
        }
        return apiResult;
    }

    private APIResult CheckCallContract(Transaction transaction) {
        byte[] payload = transaction.payload;
        byte[] from = transaction.from;
        Long amount = transaction.amount;
        byte type = payload[0];
        byte[] data = ByteUtil.bytearraycopy(payload, 1, payload.length - 1);
        //amount
        if (amount != 0) return APIResult.newFailed("Amount must be zero");
        switch (type) {
            case 0://更新合约管理员
                return CheckChangeowner(data, from);
            case 1://合约转账
                return CheckTransfer(data, from);
            case 2://增发
                return CheckIncreased(data, from);
            case 3://多签规则转账
                return CheckMultTransfer(data, transaction);
            default:
                return APIResult.newFailed("Invalid rules");
        }
    }

    private APIResult CheckDeployContract(byte[] payload, byte[] from, Long amount, byte[] topubkeyhash) {
        byte type = payload[0];
        byte[] data = ByteUtil.bytearraycopy(payload, 1, payload.length - 1);
        switch (type) {
            case 0://代币
                return CheckAsset(data, from, amount, topubkeyhash);
            case 1://多重签名
                return CheckMultiple(data, from, amount, topubkeyhash);
            default:
                return APIResult.newFailed("Invalid rules");
        }
    }

    private APIResult CheckAsset(byte[] data, byte[] from, Long amount, byte[] topubkeyhash) {
        Asset asset = new Asset();
        APIResult apiResult = new APIResult();
        if (asset.RLPdeserialization(data)) {
            byte[] createUserPublicKey = asset.getCreateuser();

            //校验
            //amount
            if (amount != 0) return APIResult.newFailed("Amount must be zero");
            //topubkeyhash
            byte[] emptyPubkeyhash = new byte[20];
            if (!Arrays.equals(emptyPubkeyhash, topubkeyhash))
                return APIResult.newFailed("topubkeyhash format check error");
            //code
            Pattern pattern = Pattern.compile("[A-Z]*");
            Matcher matcher = pattern.matcher(new String(asset.getCode()));
            //TODO 查询是否有重复code 异常处理
            if (asset.getCode().length() >= 3 && asset.getCode().length() <= 12 && matcher.matches() && !asset.getCode().equals("WDC")) {
                byte[] blockhash = wisdomRepository.getLatestConfirmed().getHash();
                if (wisdomRepository.containsAssetCodeAt(blockhash, asset.getCode().getBytes(StandardCharsets.UTF_8)))
                    return APIResult.newFailed("asset code already exists");
            } else {
                return APIResult.newFailed("Assets code format check error");
            }
            //Offering Totalamount
            if (asset.getOffering() > 0 && asset.getTotalamount() > 0) {
                if (asset.getOffering() != asset.getTotalamount()) {
                    return APIResult.newFailed("Offering and totalamount must be the same");
                }
            } else {
                return APIResult.newFailed("Offering or totalamount must be in specified scope");
            }
            //TODO 校验hash
            //fromPubkeyHash
            boolean verifyfromPubkey = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyToAddress(from, (byte) 0x00, WX)) == 0);
            if (!verifyfromPubkey) return APIResult.newFailed("From format check error");
            //Createuser
            boolean verifyCreateuser = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyToAddress(asset.getCreateuser(), (byte) 0x00, WX)) == 0);
            if (!verifyCreateuser) return APIResult.newFailed("Createuser format check error");
            //Owner
            boolean verifyOwner = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyToAddress(asset.getOwner(), (byte) 0x00, WX)) == 0);
            if (!verifyOwner) return APIResult.newFailed("Owner format check error");
            //Createuser frompubhash
            if (!Arrays.equals(from, createUserPublicKey))
                return APIResult.newFailed("Create and frompubhash are different");

            if (asset.getAllowincrease() != 0 && asset.getAllowincrease() != 1)
                return APIResult.newFailed("Allowincrease error");
            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }

    private APIResult CheckMultiple(byte[] data, byte[] from, Long amount, byte[] topubkeyhash) {
        Multiple multiple = new Multiple();
        APIResult apiResult = new APIResult();
        if (multiple.RLPdeserialization(data)) {
            //校验
            //amount
            if (amount != 0) return APIResult.newFailed("Amount must be zero");
            //topubkeyhash
            byte[] emptyPubkeyhash = new byte[20];
            if (!Arrays.equals(emptyPubkeyhash, topubkeyhash))
                return APIResult.newFailed("topubkeyhash format check error");
            //AssetHash
            if (multiple.getAssetHash().length != 20) return APIResult.newFailed("AssetHash format check error");
            byte[] WDCAssetHash = new byte[20];
            if (!Arrays.equals(multiple.getAssetHash(), WDCAssetHash)) {
                //TODO 校验AssetHash是否存在
            }
            //M and N
            if (multiple.getMin() >= 0 && multiple.getMax() >= 0) {
                if (multiple.getMin() > multiple.getMax())
                    return APIResult.newFailed("N must be less than or equal to M");
                if (!String.valueOf(multiple.getMin()).matches("[0-9]+") || !String.valueOf(multiple.getMax()).matches("[0-9]+"))
                    return APIResult.newFailed("N and max must be positive integer");
                if (multiple.getMin() < 2 || multiple.getMin() > multiple.getMax())
                    return APIResult.newFailed("N must be within the specified range");
                if (multiple.getMax() < 2 || multiple.getMax() > 8)
                    return APIResult.newFailed("M must be within the specified range");
            } else return APIResult.newFailed("N and M must be positive integer");
            //payload amount
            if (multiple.getAmount() != 0) return APIResult.newFailed("Amount must be zero");
            if (multiple.getPubList().size() != multiple.getMax())
                return APIResult.newFailed("PubkeyList does not match max");
            if (!multiple.getPubList().contains(from)) return APIResult.newFailed("From must be in payload");
            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }

    private APIResult CheckChangeowner(byte[] data, byte[] from) {
        AssetChangeowner assetChangeowner = new AssetChangeowner();
        APIResult apiResult = new APIResult();
        if (assetChangeowner.RLPdeserialization(data)) {
            //TODO hash的校验
            if (assetChangeowner.getNewowner().length != 32) {
                return APIResult.newFailed("Newowner format check error");
            }
            //fromaddress
            boolean verifyfrom = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyToAddress(from, (byte) 0x00, "")) == 0);
            if (!verifyfrom) {
                apiResult.setCode(5000);
                apiResult.setMessage("From format check error");
                return apiResult;
            }
            //TODO 查询原owner是否与from一致

            //TODO newowner与oldowner不能一致


            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }

    private APIResult CheckTransfer(byte[] data, byte[] from) {
        AssetTransfer assetTransfer = new AssetTransfer();
        APIResult apiResult = new APIResult();
        if (assetTransfer.RLPdeserialization(data)) {
            //TODO hash校验

            //FROM

            //TO

            //value
            if (assetTransfer.getValue() > 0) {
                //TODO 校验是否有足够多的余额
            } else {
                return APIResult.newFailed("Value must be greater than zero");
            }

            //From from是否一致

            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }

    private APIResult CheckIncreased(byte[] data, byte[] from) {
        AssetIncreased assetIncreased = new AssetIncreased();
        APIResult apiResult = new APIResult();
        if (assetIncreased.RLPdeserialization(data)) {
            //allowincrease
            //TODO 查询资产的allowincrease值
            //amount
            if (assetIncreased.getAmount() > 0) {
                //TODO 校验总量+增发量是否小于Long的最大值
            } else {
                return APIResult.newFailed("Amount must be greater than zero");
            }
            //From Owner
            //TODO 校验From Owner是否一致

            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }

    private APIResult CheckMultTransfer(byte[] data, Transaction transaction) {
        MultTransfer multTransfer = new MultTransfer();
        APIResult apiResult = new APIResult();
        if (multTransfer.RLPdeserialization(data)) {
            //Origin and Dest
            if (multTransfer.getOrigin() != 0 && multTransfer.getOrigin() != 1)
                return APIResult.newFailed("Origin must be within the specified range");
            if (multTransfer.getDest() != 0 && multTransfer.getDest() != 1)
                return APIResult.newFailed("Dest must be within the specified range");
            if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 0)
                return APIResult.newFailed("Dest and origin cannot be both normal address");
            if (multTransfer.getOrigin() == 0) {//from是普通地址

            } else {
                if (multTransfer.getFrom().size() != multTransfer.getSignatures().size())
                    return APIResult.newFailed("The number of pubkey and sign is not the same");
                //payload from
                List<byte[]> from = new ArrayList<>();
                //TODO 查询部署时多签的n,区别普通与多签
                int n = 0;
                if (from.size() < n) return APIResult.newFailed("Sign numbers less than n");
                byte[] nonece = {};
                if (multTransfer.getOrigin() == 0) {//普通地址
                    from = multTransfer.getFrom();
                    nonece = BigEndian.encodeUint64(transaction.nonce);
                } else {
                    //去重   TODO 查询部署时多签的pubkey List 取交集
                    from = multTransfer.getFrom().stream().distinct().collect(Collectors.toList());
                    nonece = BigEndian.encodeUint64(0);
                }
                if (!from.contains(transaction.from)) return APIResult.newFailed("From must be in payload");
                //signatures 去重
                List<byte[]> signatures = multTransfer.getSignatures().stream().distinct().collect(Collectors.toList());
                int signAdopt = 0;
                //构造签名原文
                byte[] version = new byte[1];
                version[0] = (byte) transaction.version;
                byte[] type = new byte[1];
                type[0] = (byte) transaction.type;
                byte[] nullsig = new byte[64];
                //TODO 确认gas
                byte[] gasPrice = ByteUtil.longToBytes(obtainServiceCharge(100000L, serviceCharge));
                byte[] amount = ByteUtil.longToBytes(0L);
                //验证签名
                for (int i = 0; i < from.size(); i++) {
                    Ed25519PublicKey ed25519PublicKey = new Ed25519PublicKey(from.get(i));
                    for (int j = 0; j < signatures.size(); j++) {
                        List<byte[]> fromList = new ArrayList<>();
                        fromList.add(from.get(i));
                        List<byte[]> emptyList = new ArrayList<>();
                        MultTransfer payloadMultTransfer = new MultTransfer(multTransfer.getOrigin(), multTransfer.getDest(), fromList, emptyList, multTransfer.getTo(), multTransfer.getValue());
                        byte[] nosig = ByteUtil.merge(version, type, nonece, from.get(i), gasPrice, amount, nullsig, transaction.to, BigEndian.encodeUint32(payloadMultTransfer.RLPserialization().length), payloadMultTransfer.RLPserialization());
                        if (ed25519PublicKey.verify(nosig, signatures.get(j))) signAdopt++;
                    }
                }
                //TODO 查询部署时多签的n
                if (signAdopt < n) return APIResult.newFailed("Sign numbers less than n");

                //TODO 验证to

                //value
                //TODO 查询FROM TO币种是否一致

                if (!String.valueOf(multTransfer.getValue()).matches("[0-9]+"))
                    return APIResult.newFailed("Value must be positive integer");
                //TODO 查询DB验证余额是否足够


            }
            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }


    private APIResult CheckHatch(long amount, byte[] payload, byte[] topubkeyhash) {
        APIResult apiResult = new APIResult();
        try {
            //本金校验
            if (amount < 30000000000L) {
                apiResult.setCode(5000);
                apiResult.setMessage("Minimum incubation amount: 300WDC");
                return apiResult;
            }
            HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(payload);
            int days = payloadproto.getType();
            if (days != 120 && days != 365) {
                apiResult.setCode(5000);
                apiResult.setMessage("Abnormal incubation days");
                return apiResult;
            }
            long nowheight = wisdomRepository.getBestBlock().nHeight;
            String nowrate = rateTable.selectrate(nowheight, days);
            //利息和分享收益
            BigDecimal amountbig = BigDecimal.valueOf(amount);
            BigDecimal ratebig = new BigDecimal(nowrate);
            BigDecimal onemut = amountbig.multiply(ratebig);
            BigDecimal daysbig = BigDecimal.valueOf(days);
            long interest = onemut.multiply(daysbig).longValue();
            String sharpub = payloadproto.getSharePubkeyHash();
            byte[] sharbyte = Hex.decodeHex(sharpub);
            if (Arrays.equals(sharbyte, topubkeyhash)) {
                apiResult.setCode(5000);
                apiResult.setMessage("You cannot promote yourself as a sharer");
                return apiResult;
            }
            if (sharpub != null && sharpub != "") {
                if (Hex.decodeHex(sharpub.toCharArray()).length != 20) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Sharer format error");
                    return apiResult;
                } else {
                    long sharIncome = (long) (interest * 0.1);
                    interest = interest + sharIncome;
                }
            }
            //查询总地址余额
            Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(IncubatorAddress.resultpubhash());
            if (!accountStateOptional.isPresent()) {
                apiResult.setCode(5000);
                apiResult.setMessage("Hatch total account abnormal");
                return apiResult;
            }
            long total = accountStateOptional.get().getAccount().getBalance();
            if (total < interest) {
                apiResult.setCode(5000);
                apiResult.setMessage("The incubation amount is paid out");
                return apiResult;
            }
        } catch (Exception e) {
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    private APIResult CheckInterAndShare(int type, long amount, byte[] payload, Incubator incubator, byte[] topubkeyhash) {
        APIResult apiResult = new APIResult();
        try {
            if (payload.length != 32) {//事务哈希
                apiResult.setCode(5000);
                apiResult.setMessage("Incorrect transaction hash format");
                return apiResult;
            }
            Transaction transaction = wisdomBlockChain.getTransaction(payload);
            if (transaction == null) {
                apiResult.setCode(5000);
                apiResult.setMessage("The initial incubation transaction could not be queried");
                return apiResult;
            }
            byte[] tranpayload = transaction.payload;
            HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(tranpayload);
            int days = payloadproto.getType();
            String rate = rateTable.selectrate(transaction.height, days);
            long capital = transaction.amount;
            BigDecimal capitalbig = BigDecimal.valueOf(capital);
            BigDecimal ratebig = new BigDecimal(rate);
            BigDecimal totalratebig = capitalbig.multiply(ratebig);
            if (incubator == null) {
                apiResult.setCode(5000);
                apiResult.setMessage("Unable to query incubation status");
                return apiResult;
            }
            //每天可提取
            long totalrate = totalratebig.longValue();
            if (totalrate == 0) {
                apiResult.setCode(5000);
                apiResult.setMessage("No amount can be withdrawn per day");
                return apiResult;
            }
            //最后提取时间
            long inheight = 0;
            long nowincub = 0;
            if (type == 0x0b) {//提取分享收益
                if (!Arrays.equals(topubkeyhash, incubator.getShare_pubkeyhash())) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The account is inconsistent with the hatcher-sharing user");
                    return apiResult;
                }
                if (incubator.getShare_amount() == 0 || incubator.getShare_amount() < amount) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The sharing income cannot be withdrawn or is greater than the amount that can be withdrawn");
                    return apiResult;
                }
                BigDecimal bl = BigDecimal.valueOf(0.1);
                BigDecimal b2 = BigDecimal.valueOf(totalrate);
                BigDecimal totalratebigs = b2.multiply(bl);
                totalrate = totalratebigs.longValue();
                inheight = incubator.getLast_blockheight_share();
                nowincub = incubator.getShare_amount();
            } else {//提取利息
                if (!Arrays.equals(topubkeyhash, incubator.getPubkeyhash())) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The account is inconsistent with the incubator user");
                    return apiResult;
                }
                if (incubator.getInterest_amount() == 0 || incubator.getInterest_amount() < amount) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Interest income cannot be withdrawn or is greater than the amount that can be withdrawn");
                    return apiResult;
                }
                inheight = incubator.getLast_blockheight_interest();
                nowincub = incubator.getInterest_amount();
            }
            if (totalrate > amount) {//amount小于最小每天可提取
                if (nowincub < totalrate) {
                    if (amount != nowincub) {
                        apiResult.setCode(5000);
                        apiResult.setMessage("Abnormal withdrawal amount");
                        return apiResult;
                    }
                } else if (nowincub == totalrate) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Abnormal withdrawal amount");
                    return apiResult;
                } else {
                    if (transaction.height > 40000) {
                        long muls = (long) (nowincub % totalrate);
                        if (muls != 0) {//数据不对
                            long syamount = muls;
                            if (syamount != amount) {
                                apiResult.setCode(5000);
                                apiResult.setMessage("Abnormal withdrawal amount");
                                return apiResult;
                            }
                        } else {
                            apiResult.setCode(5000);
                            apiResult.setMessage("Abnormal withdrawal amount");
                            return apiResult;
                        }
                    }
                }
            } else {
                //天数
                long remainder = (long) (amount % totalrate);
                if (remainder != 0) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The withdrawal amount is not a multiple of the daily withdrawal amount");
                    return apiResult;
                }
                int mul = (int) (amount / totalrate);
                if (mul == 0) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Cannot extract, still less than a day");
                    return apiResult;
                }
                long nowheight = wisdomRepository.getBestBlock().nHeight;
                int blockcount = mul * configuration.getDay_count(nowheight);
                if ((inheight + blockcount) > nowheight) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("In excess of the amount available");
                    return apiResult;
                }
            }
        } catch (Exception e) {
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    private APIResult CheckDeposit(byte[] payload) {
        APIResult apiResult = new APIResult();
        if (payload.length > 1000) {
            apiResult.setCode(5000);
            apiResult.setMessage("Memory cannot exceed 1000 bytes");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    private APIResult CheckCost(long amount, byte[] payload, Incubator incubator, byte[] topubkeyhash) {
        APIResult apiResult = new APIResult();
        if (payload.length != 32) {//事务哈希
            apiResult.setCode(5000);
            apiResult.setMessage("Incubate transaction format errors");
            return apiResult;
        }
        if (incubator == null) {
            apiResult.setCode(5000);
            apiResult.setMessage("Unable to query incubation status");
            return apiResult;
        }
        if (!Arrays.equals(incubator.getPubkeyhash(), topubkeyhash)) {
            apiResult.setCode(5000);
            apiResult.setMessage("This hatch is not yours, you can't get the principal out");
            return apiResult;
        }
        if (incubator.getInterest_amount() != 0 || incubator.getCost() == 0) {
            apiResult.setCode(5000);
            apiResult.setMessage("The incubation principal has been withdrawn or the interest has not yet been withdrawn");
            return apiResult;
        }
        if (amount != incubator.getCost() || amount == 0) {
            apiResult.setCode(5000);
            apiResult.setMessage("Wrong amount of principal withdrawal");
            return apiResult;
        }
        Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(topubkeyhash);
        if (!accountState.isPresent()) {
            apiResult.setCode(5000);
            apiResult.setMessage("This account is abnormal");
            return apiResult;
        }
        Account account = accountState.get().getAccount();
        if (account.getIncubatecost() < 0 || account.getIncubatecost() < amount) {
            apiResult.setCode(5000);
            apiResult.setMessage("The withdrawal amount is incorrect");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    private APIResult CheckRecallVote(long amount, byte[] payload, byte[] from, byte[] topubkeyhash) {
        APIResult apiResult = new APIResult();
        if (payload.length != 32) {//投票事务哈希
            apiResult.setCode(5000);
            apiResult.setMessage("The voting transaction payload was incorrectly formatted");
            return apiResult;
        }
        Block best = wisdomRepository.getBestBlock();
        boolean hasvote = wisdomRepository.containsTransactionAt(best.getHash(), payload);
        if (!hasvote) {
            apiResult.setCode(5000);
            apiResult.setMessage("The vote has been withdrawn");
            return apiResult;
        }
        Transaction transaction = wisdomBlockChain.getTransaction(payload);
        if (transaction == null) {
            apiResult.setCode(5000);
            apiResult.setMessage("Unable to get vote transaction");
            return apiResult;
        }
        if (transaction.type != Transaction.Type.VOTE.ordinal()) {
            apiResult.setCode(5000);
            apiResult.setMessage("The type of withdrawal is not a vote");
            return apiResult;
        }
        if (!Arrays.equals(transaction.from, from) || !Arrays.equals(transaction.to, topubkeyhash)) {
            apiResult.setCode(5000);
            apiResult.setMessage("You have to withdraw your vote");
            return apiResult;
        }
        if (transaction.amount != amount) {
            apiResult.setCode(5000);
            apiResult.setMessage("The number of votes is not correct");
            return apiResult;
        }
        Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(topubkeyhash);
        if (!accountStateOptional.isPresent()) {
            apiResult.setCode(5000);
            apiResult.setMessage("This account is abnormal");
            return apiResult;
        }
        Account account = accountStateOptional.get().getAccount();
        if (account.getVote() < 0 || account.getVote() < amount) {
            apiResult.setCode(5000);
            apiResult.setMessage("The withdrawal amount is incorrect");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }


    private APIResult CheckRecallMortgage(long amount, byte[] payload, byte[] topubkeyhash) {
        APIResult apiResult = new APIResult();
        if (payload.length == 0) {
            return APIResult.newFailResult(5000, "recall mortgage payload cannot null");
        }
        if (payload.length != 32) {//抵押事务哈希
            apiResult.setCode(5000);
            apiResult.setMessage("The mortgage transaction payload was incorrectly formatted");
            return apiResult;
        }
        Block best = wisdomRepository.getBestBlock();
        boolean hasmortgage = wisdomRepository.containsTransactionAt(best.getHash(), payload);
        if (!hasmortgage) {
            apiResult.setCode(5000);
            apiResult.setMessage("The mortgage has been withdrawn");
            return apiResult;
        }
        Transaction transaction = wisdomBlockChain.getTransaction(payload);
        if (transaction == null) {
            apiResult.setCode(5000);
            apiResult.setMessage("Unable to get mortgage transaction");
            return apiResult;
        }
        if (transaction.type != Transaction.Type.MORTGAGE.ordinal()) {
            apiResult.setCode(5000);
            apiResult.setMessage("The type of withdrawal is not mortgage");
            return apiResult;
        }
        if (!Arrays.equals(transaction.to, topubkeyhash)) {
            apiResult.setCode(5000);
            apiResult.setMessage("You have to withdraw your mortgage");
            return apiResult;
        }
        if (transaction.amount != amount) {
            apiResult.setCode(5000);
            apiResult.setMessage("The number of mortgage is not correct");
            return apiResult;
        }
        Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(topubkeyhash);
        if (!accountStateOptional.isPresent()) {
            apiResult.setCode(5000);
            apiResult.setMessage("Unable to withdraw");
            return apiResult;
        }
        Account account = accountStateOptional.get().getAccount();
        if (account.getMortgage() < amount) {
            apiResult.setCode(5000);
            apiResult.setMessage("The withdrawal amount is incorrect");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    public boolean checkoutPool(Transaction t, AccountState accountState) {
        if (accountState.getAccount() == null) {
            return false;
        }
        Incubator incubator = CommandServiceImpl.getIncubator(accountState, t.type, t.payload);
        APIResult apiResult = TransactionVerify(t, accountState.getAccount(), incubator);
        if (apiResult.getCode() == 5000) {
            logger.info("Queued to Pending, memory pool check error, tx:" + t.getHashHexString() + ", " + apiResult.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 计算gas单价
     *
     * @param gas
     * @param total
     * @return
     */
    public static Long obtainServiceCharge(Long gas, Long total) {
        BigDecimal a = new BigDecimal(gas.toString());
        BigDecimal b = new BigDecimal(total.toString());
        BigDecimal divide = b.divide(a, 0, RoundingMode.HALF_UP);
        Long gasPrice = divide.longValue();
        return gasPrice;
    }
}

