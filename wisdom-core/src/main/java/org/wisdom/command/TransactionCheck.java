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
import org.apache.commons.codec.DecoderException;
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
import org.wisdom.contract.HashheightblockDefinition.Hashheightblock;
import org.wisdom.contract.HashheightblockDefinition.HashheightblockGet;
import org.wisdom.contract.HashheightblockDefinition.HashheightblockTransfer;
import org.wisdom.contract.HashtimeblockDefinition.Hashtimeblock;
import org.wisdom.contract.HashtimeblockDefinition.HashtimeblockGet;
import org.wisdom.contract.HashtimeblockDefinition.HashtimeblockTransfer;
import org.wisdom.contract.MultipleDefinition.MultTransfer;
import org.wisdom.contract.MultipleDefinition.Multiple;
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

    @Autowired
    WisdomRepository repository;

    public static final String WX = "WX";
    public static final String WR = "WR";

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
            //hatch disabled
            if (type[0] == 9) {
                apiResult.setCode(5000);
                apiResult.setMessage("Hatching transactions have been disabled");
                return apiResult;
            }
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
            byte[] emptyPubkeyhash = new byte[20];
            if (!Arrays.equals(emptyPubkeyhash, topubkeyhash) && type[0] == Transaction.Type.DEPLOY_CONTRACT.ordinal()) {
                apiResult.setCode(5000);
                apiResult.setMessage("To must be zero");
                return apiResult;
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
                apiResult = CheckDeployContract(payload, from, amount, topubkeyhash, transaction);
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
                return CheckChangeowner(data, transaction);
            case 1://合约转账
                return CheckTransfer(data, transaction);
            case 2://增发
                return CheckIncreased(data, transaction);
            case 3://多签规则转账
                return CheckMultTransfer(data, transaction);
            case 4://哈希时间锁定转账
                return CheckHashtimeblockTransfer(data, transaction);
            case 5://哈希时间锁定获取
                return CheckHashtimeblockGet(data, transaction);
            case 6://哈希高度锁定转账
                return CheckHashheightblockTransfer(data, transaction);
            case 7://哈希高度锁定获取
                return CheckHashheightblockGet(data, transaction);
            default:
                return APIResult.newFailed("Invalid rules");
        }
    }

    private APIResult CheckDeployContract(byte[] payload, byte[] from, Long amount, byte[] topubkeyhash, Transaction transaction) {
        byte type = payload[0];
        byte[] data = ByteUtil.bytearraycopy(payload, 1, payload.length - 1);
        switch (type) {
            case 0://代币
                return CheckAsset(data, from, amount, topubkeyhash);
            case 1://多重签名
                return CheckMultiple(data, transaction);
            case 2://哈希时间锁定
                return CheckHashtimeblock(data, from);
            case 3://哈希高度锁定
                return CheckHashheightblock(data, from);
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
            //code 是否重复
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
            //Offering
            //求余
            long remainder = asset.getOffering() % EconomicModel.WDC;
            if (remainder != 0) {
                return APIResult.newFailed("Offering must be an integer");
            }
            //fromPubkey
            if (!KeystoreAction.verifyPublickey(from))
                return APIResult.newFailed("From format check error");
            //Owner
            if (asset.getOwner().length != 20)
                return APIResult.newFailed("Owner format check error");
            //Createuser frompubhash
            if (!Arrays.equals(from, createUserPublicKey))
                return APIResult.newFailed("Create and frompubhash are different");

            if (asset.getAllowincrease() != 0 && asset.getAllowincrease() != 1)
                return APIResult.newFailed("Allowincrease error");
            //info
            if (asset.getInfo().getBytes(StandardCharsets.UTF_8).length > 300)
                return APIResult.newFailed("Info out of specified range");
            apiResult.setCode(2000);
            apiResult.setMessage("SUCCESS");
            return apiResult;
        }
        return APIResult.newFailed("Invalid Assets rules");
    }

    private APIResult CheckMultiple(byte[] data, Transaction transaction) {
        byte[] from = transaction.from;
        Long amount = transaction.amount;
        byte[] topubkeyhash = transaction.to;
        Multiple multiple = new Multiple();
        if (multiple.RLPdeserialization(data)) {
            //校验
            //amount
            if (amount != 0) return APIResult.newFailed("Amount must be zero");
            //topubkeyhash
            byte[] emptyPubkeyhash = new byte[20];
            if (!Arrays.equals(emptyPubkeyhash, topubkeyhash) || topubkeyhash.length != 20)
                return APIResult.newFailed("topubkeyhash format check error");
            //AssetHash
            if (multiple.getAssetHash().length != 20) return APIResult.newFailed("AssetHash format check error");
            byte[] WDCAssetHash = new byte[20];
            if (!Arrays.equals(multiple.getAssetHash(), WDCAssetHash)) {
                //校验AssetHash是否存在
                Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(multiple.getAssetHash());
                if (accountState.isPresent()) return APIResult.newFailed("AssetHash already exists");
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
            //pubkeyList
            if (ByteUtil.byteListsDistinct(multiple.getPubList()).size() != multiple.getMax())
                return APIResult.newFailed("PubkeyList do not match max");
            if (!ByteUtil.byteListContains(multiple.getPubList(),from)) return APIResult.newFailed("From must be in payload");
            //signatureList
            if (ByteUtil.byteListsDistinct(multiple.getSignatureList()).size() != multiple.getMax())
                return APIResult.newFailed("SignatureList() do not match max");
            //构造签名原文
            byte[] from_version = new byte[1];
            from_version[0] = (byte) transaction.version;
            byte[] from_type = new byte[1];
            from_type[0] = (byte) transaction.type;
            byte[] from_nonce = new byte[1];
            from_nonce =  ByteUtil.longToBytes(0L);
            byte[] from_nullsig = new byte[64];
            byte[] from_gasPrice = ByteUtil.longToBytes(transaction.gasPrice);
            byte[] from_amount = ByteUtil.longToBytes(0L);
            Multiple from_multiple = new Multiple();
            from_multiple.setAssetHash(multiple.getAssetHash());
            from_multiple.setMax(multiple.getMax());
            from_multiple.setMin(multiple.getMin());
            from_multiple.setPubList(multiple.getPubList());
            from_multiple.setAmount(multiple.getAmount());
            from_multiple.setSignatureList(new ArrayList<>());
            byte[] from_payload = from_multiple.RLPserialization();
            from_payload = ByteUtil.merge(new byte[]{0x01},from_payload);
            byte[] payloadLength = ByteUtil.intToBytes(from_payload.length);
            byte[] from_allPayload =ByteUtil.merge(payloadLength,from_payload);
            byte[] nosig = ByteUtil.merge(from_version, from_type, from_nonce, from, from_gasPrice, from_amount, from_nullsig, topubkeyhash, from_allPayload);
            Ed25519PublicKey ed25519PublicKey = new Ed25519PublicKey(from);
            if (!Arrays.equals(from,multiple.getPubList().get(0)) || !ed25519PublicKey.verify(nosig, multiple.getSignatureList().get(0)))
                return APIResult.newFailed("The first from of fromPubkeyList or SignatureList is different from From");
            List<byte[]> pubkeylist = ByteUtil.byteListsDistinct(multiple.getPubList());
            List<byte[]> signatureList = ByteUtil.byteListsDistinct(multiple.getSignatureList());
            int number = 0;
            //验证签名
            for (int i=0;i<pubkeylist.size();i++){
                Ed25519PublicKey ed25519PublicKey_payload = new Ed25519PublicKey(pubkeylist.get(i));
                    for (int j=0;j<signatureList.size();j++){
                        if (ed25519PublicKey_payload.verify(nosig,multiple.getSignatureList().get(j))){
                            number++;
                        }
                    }
            }
            if (number != multiple.getMax())
                return APIResult.newFailed("Not enough Pubkey or Signature");
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid Multiple rules");
    }

    private APIResult CheckHashtimeblock(byte[] data, byte[] frompubhash) {
        Hashtimeblock hashtimeblock = new Hashtimeblock();
        byte[] emptyBytes = new byte[20];
        if (hashtimeblock.RLPdeserialization(data)) {
            if (!Arrays.equals(hashtimeblock.getAssetHash(), emptyBytes)) {
                //校验如果不是WDC校验区块中是否存在
                Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(hashtimeblock.getAssetHash());
                if (!accountState.isPresent()) return APIResult.newFailed("AssetHash do not exist");
            }
            //pubkeyhash 为普通账户地址的公钥哈希
            if (hashtimeblock.getPubkeyHash().length != 20)
                return APIResult.newFailed("To must be Ordinary address");
            Optional<AccountState> accountStateTo = wisdomRepository.getConfirmedAccountState(hashtimeblock.getPubkeyHash());
            if (accountStateTo.isPresent()) {
                if (accountStateTo.get().getType() != 0)
                    return APIResult.newFailed("To must be Ordinary address");
            }
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid Hashtimeblock rules");
    }

    private APIResult CheckHashheightblock(byte[] data, byte[] frompubhash) {
        Hashheightblock hashheightblock = new Hashheightblock();
        byte[] emptyBytes = new byte[20];
        if (hashheightblock.RLPdeserialization(data)) {
            if (!Arrays.equals(hashheightblock.getAssetHash(), emptyBytes)) {
                //校验如果不是WDC校验区块中是否存在
                Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(hashheightblock.getAssetHash());
                if (!accountState.isPresent()) return APIResult.newFailed("AssetHash do not exist");
            }
            //pubkeyhash 为普通账户地址的公钥哈希
            if (hashheightblock.getPubkeyHash().length != 20)
                return APIResult.newFailed("To must be Ordinary address");
            Optional<AccountState> accountStateTo = wisdomRepository.getConfirmedAccountState(hashheightblock.getPubkeyHash());
            if (accountStateTo.isPresent()) {
                if (accountStateTo.get().getType() != 0)
                    return APIResult.newFailed("To must be Ordinary address");
            }
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid Hashheightblock rules");
    }

    private APIResult CheckChangeowner(byte[] data, Transaction transaction) {
        AssetChangeowner assetChangeowner = new AssetChangeowner();
        if (assetChangeowner.RLPdeserialization(data)) {
            if (assetChangeowner.getNewowner().length != 20) {
                return APIResult.newFailed("Newowner format check error");
            }
            Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(transaction.to);
            //AssetHash是否存在
            if (!accountState.isPresent()) return APIResult.newFailed("Asset do not exist");
            if (accountState.get().getType() != 1) return APIResult.newFailed("Must fill in the correct asset");
            Asset asset = new Asset();
            if (asset.RLPdeserialization(accountState.get().getContract())) {
                //查询原owner是否与from一致
                if (!Arrays.equals(asset.getOwner(), KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from)))
                    return APIResult.newFailed("From must be the same as original owner");
                //newowner与oldowner不能一致
                if (Arrays.equals(asset.getOwner(), assetChangeowner.getNewowner()))
                    return APIResult.newFailed("New owner must be different from original owner");
                //newowner必须是普通地址
                Optional<AccountState> accountStateNewowner = wisdomRepository.getConfirmedAccountState(assetChangeowner.getNewowner());
                if (accountStateNewowner.isPresent()) {
                    if (accountStateNewowner.get().getType() != 0)
                        return APIResult.newFailed("New owner must be Ordinary address");
                }
            } else {
                return APIResult.newFailed("Invalid Assets rules");
            }
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid AssetsChangeowner rules");
    }

    private APIResult CheckTransfer(byte[] data, Transaction transaction) {
        AssetTransfer assetTransfer = new AssetTransfer();
        if (assetTransfer.RLPdeserialization(data)) {
            //判断160哈希值是否已经存在
            Optional<AccountState> contractAccountState = wisdomRepository.getConfirmedAccountState(transaction.to);
            //AssetHash是否存在
            if (!contractAccountState.isPresent()) return APIResult.newFailed("Asset do not exist");
            if (contractAccountState.get().getType() != 1) return APIResult.newFailed("Must fill in the correct asset");
            Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from));
            if (!accountState.isPresent()) return APIResult.newFailed("From do not exist");
            //value
            if (assetTransfer.getValue() > 0) {
                //校验是否有足够多的余额
                byte[] emptyBytes = new byte[20];
                if (Arrays.equals(emptyBytes, assetTransfer.getTo())) {//WDC
                    if (assetTransfer.getValue() > accountState.get().getAccount().getBalance())
                        return APIResult.newFailed("Insufficient funds");
                } else {
                    if (accountState.get().getTokensMap().size() == 0) {
                        return APIResult.newFailed("Insufficient funds");
                    }
                    if (assetTransfer.getValue() > (accountState.get().getTokensMap().get(transaction.to) == null ? 0 : accountState.get().getTokensMap().get(transaction.to)))
                        return APIResult.newFailed("Insufficient funds");
                }
            } else {
                return APIResult.newFailed("Value must be greater than zero");
            }
            //From from是否一致
            if (!Arrays.equals(transaction.from, assetTransfer.getFrom()))
                return APIResult.newFailed("From and from must be the same");
            //from From 不能为多签地址
            Optional<AccountState> fromAccountState = wisdomRepository.getConfirmedAccountState(KeystoreAction.pubkeybyteToPubkeyhashbyte(assetTransfer.getFrom()));
            if (!fromAccountState.isPresent())
                return APIResult.newFailed("From do not exist");
            if (fromAccountState.get().getType() != 0)
                return APIResult.newFailed("Payload from must be Ordinary address");
            if (accountState.get().getType() != 0) return APIResult.newFailed("From must be Ordinary address");
            Optional<AccountState> toAccountState = wisdomRepository.getConfirmedAccountState(KeystoreAction.pubkeybyteToPubkeyhashbyte(assetTransfer.getTo()));
            if (toAccountState.isPresent()) {//如果存在 必须为普通地址
                if (toAccountState.get().getType() != 0)
                    return APIResult.newFailed("To must be Ordinary address");
            }
            //to 为公钥哈希
            if (assetTransfer.getTo().length != 20)
                return APIResult.newFailed("Payload to format error");
            //to为普通地址
            Optional<AccountState> accountStateNewowner = wisdomRepository.getConfirmedAccountState(assetTransfer.getTo());
            if (accountStateNewowner.isPresent()) {
                if (accountStateNewowner.get().getType() != 0)
                    return APIResult.newFailed("Payload to must be Ordinary address");
            }
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid AssetsTransfer rules");
    }

    private APIResult CheckIncreased(byte[] data, Transaction transaction) {
        AssetIncreased assetIncreased = new AssetIncreased();
        if (assetIncreased.RLPdeserialization(data)) {
            //allowincrease
            Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(transaction.to);
            if (!accountState.isPresent())
                return APIResult.newFailed("Asset do not exist");
            if (accountState.get().getType() != 1)
                return APIResult.newFailed("Must fill in the correct asset");
            Asset asset = new Asset();
            if (asset.RLPdeserialization(accountState.get().getContract())) {
                //查询资产的allowincrease值
                if (asset.getAllowincrease() == 0) return APIResult.newFailed("Asset not allow Increased");
                //From Owner是否一致
                if (!Arrays.equals(asset.getOwner(), KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from)))
                    return APIResult.newFailed("From and owner must be the same");
            } else {
                return APIResult.newFailed("Invalid Assets rules");
            }
            //amount
            if (assetIncreased.getAmount() > 0) {
                //校验总量+增发量是否小于Long的最大值
                if (assetIncreased.getAmount() + asset.getTotalamount() < 0)
                    return APIResult.newFailed("Amount maximum exceeded");
            } else {
                return APIResult.newFailed("Amount must be greater than zero");
            }
            //amount求余
            long remainder = assetIncreased.getAmount() % EconomicModel.WDC;
            if (remainder != 0) {
                return APIResult.newFailed("Amount must be an integer");
            }

            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid AssetsIncreased rules");
    }

    private APIResult CheckMultTransfer(byte[] data, Transaction transaction) {
        MultTransfer multTransfer = new MultTransfer();
        if (multTransfer.RLPdeserialization(data)) {
            //不能给自己转
            if (!Arrays.equals(multTransfer.getTo(), transaction.to))
                return APIResult.newFailed("From and to can't be the same");
            //Origin and Dest
            if (multTransfer.getOrigin() != 0 && multTransfer.getOrigin() != 1)
                return APIResult.newFailed("Origin must be within the specified range");
            if (multTransfer.getDest() != 0 && multTransfer.getDest() != 1)
                return APIResult.newFailed("Dest must be within the specified range");
            if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 0)
                return APIResult.newFailed("Dest and origin cannot be both normal address");
            Optional<AccountState> accountStateFrom = wisdomRepository.getConfirmedAccountState(KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from));
            if (!accountStateFrom.isPresent())
                return APIResult.newFailed("From do not exist");
            Optional<AccountState> accountStateTo = wisdomRepository.getConfirmedAccountState(transaction.to);
            if (!accountStateTo.isPresent())
                return APIResult.newFailed("Multiple do not exist");
            if (accountStateTo.get().getType() != 2)
                return APIResult.newFailed("Must fill in the correct multiple");
            Optional<AccountState> accountStatePayloadTo = wisdomRepository.getConfirmedAccountState(multTransfer.getTo());

            if (multTransfer.getOrigin() == 0) {
                if (accountStateFrom.get().getType() != 0) return APIResult.newFailed("Origin error in type");
            } else {
                if (accountStateFrom.get().getType() != 2) return APIResult.newFailed("Origin error in type");
            }
            if (multTransfer.getDest() == 0) {
                if (accountStateTo.get().getType() != 0) return APIResult.newFailed("Dest error in type");
            } else {
                if (accountStateTo.get().getType() != 2) return APIResult.newFailed("Dest error in type");
            }
            byte[] WDCbyte = new byte[20];
            if (multTransfer.getOrigin() == 0) {//from是普通地址 普通->多签 看TO的规则
                //多签规则
                Multiple multiple = new Multiple();
                if (multiple.RLPdeserialization(transaction.to)) {
                    //代币类型
                    byte[] assetHash = multiple.getAssetHash();
                    if (Arrays.equals(assetHash, WDCbyte)) {//WDC
                        if (accountStateFrom.get().getAccount().getBalance() < multTransfer.getValue())
                            return APIResult.newFailed("Insufficient funds");
                    } else {//其他代币
                        if (accountStateFrom.get().getTokensMap().size() == 0)
                            return APIResult.newFailed("Insufficient funds");
                        if ((accountStateFrom.get().getTokensMap().get(assetHash) == null ? 0 : accountStateFrom.get().getTokensMap().get(assetHash)) < multTransfer.getValue())
                            return APIResult.newFailed("Insufficient funds");
                    }
                } else {
                    return APIResult.newFailed("Invalid Multiple rules");
                }
            } else {//多签->普通 多签->多签 看From的规则
                //多签规则
                Multiple multiple = new Multiple();
                if (multiple.RLPdeserialization(KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from))) {
                    if (multTransfer.getFrom().size() != multTransfer.getSignatures().size())
                        return APIResult.newFailed("The number of pubkey and sign is not the same");
                    //payload from
                    List<byte[]> payload_from = new ArrayList<>();
                    List<byte[]> from = new ArrayList<>();
                    int n = multiple.getMin();
                    if (from.size() < n) return APIResult.newFailed("Sign numbers less than n");
                    byte[] nonece = {};
                    if (multTransfer.getOrigin() == 1) {
                        //查询部署时多签的pubkey List
                        List<byte[]> pubkeylist = ByteUtil.byteListsDistinct(multiple.getPubList());
                        //去重 取交集
                        payload_from = ByteUtil.byteListsDistinct(multTransfer.getFrom());
                        from = ByteUtil.byteListsIntersection(pubkeylist,payload_from);
                        nonece = BigEndian.encodeUint64(0);
                    }

                    if (! ByteUtil.byteListContains(from,transaction.from)) return APIResult.newFailed("From must be in payload");
                    //signatures 去重
                    List<byte[]> signatures = ByteUtil.byteListsDistinct(multTransfer.getSignatures());
                    int signAdopt = 0;
                    //构造签名原文
                    byte[] version = new byte[1];
                    version[0] = (byte) transaction.version;
                    byte[] type = new byte[1];
                    type[0] = (byte) transaction.type;
                    byte[] nullsig = new byte[64];
                    byte[] gasPrice = ByteUtil.longToBytes(transaction.gasPrice);
                    byte[] amount = ByteUtil.longToBytes(0L);
                    //验证签名
                    Ed25519PublicKey from_ed25519PublicKey = new Ed25519PublicKey(transaction.from);
                    List<byte[]> emptyList = new ArrayList<>();
                    MultTransfer payloadMultTransfer = new MultTransfer(multTransfer.getOrigin(), multTransfer.getDest(), multTransfer.getFrom(), emptyList, multTransfer.getTo(), multTransfer.getValue());
                    byte[] nosig = ByteUtil.merge(version, type, nonece, transaction.from, gasPrice, amount, nullsig, transaction.to, BigEndian.encodeUint32(payloadMultTransfer.RLPserialization().length+1),new byte[]{0x03}, payloadMultTransfer.RLPserialization());
                    if (!Arrays.equals(transaction.from,multTransfer.getFrom().get(0)) || !from_ed25519PublicKey.verify(nosig, multiple.getSignatureList().get(0)))
                        return APIResult.newFailed("The first from of fromPubkeyList or SignatureList is different from From");

                    for (int i = 0; i < from.size(); i++) {
                        Ed25519PublicKey ed25519PublicKey = new Ed25519PublicKey(from.get(i));
                        for (int j = 0; j < signatures.size(); j++) {
                            if (ed25519PublicKey.verify(nosig, signatures.get(j)))
                                signAdopt++;
                        }
                    }
                    if (signAdopt < n) return APIResult.newFailed("Sign numbers less than n");
                    //多签->多签 查询FROM TO币种是否一致
                    if (multTransfer.getOrigin() == 1 && multTransfer.getDest() == 1) {
                        Multiple multiplePayloadTo = new Multiple();
                        if (!accountStatePayloadTo.isPresent())
                            return APIResult.newFailed("To multiple do not exist");
                        if (multiple.RLPdeserialization(accountStatePayloadTo.get().getContract())) {
                            if (!Arrays.equals(multiple.getAssetHash(), multiplePayloadTo.getAssetHash()))
                                return APIResult.newFailed("Must be the same currency");
                        } else {
                            return APIResult.newFailed("Invalid Multiple rules");
                        }

                    }
                    multTransfer.getTo();
                    if (!String.valueOf(multTransfer.getValue()).matches("[0-9]+"))
                        return APIResult.newFailed("Value must be positive integer");
                    //代币类型
                    byte[] assetHash = multiple.getAssetHash();
                    //验证余额是否足够
                    if (Arrays.equals(assetHash, WDCbyte)) {//WDC
                        if (accountStateFrom.get().getAccount().getBalance() < multTransfer.getValue())
                            return APIResult.newFailed("Insufficient funds");
                    } else {//其他代币
                        if (accountStateFrom.get().getTokensMap().size() == 0)
                            return APIResult.newFailed("Insufficient funds");
                        if ((accountStateFrom.get().getTokensMap().get(assetHash) == null ? 0 : accountStateFrom.get().getTokensMap().get(assetHash)) < multTransfer.getValue())
                            return APIResult.newFailed("Insufficient funds");
                    }

                } else {
                    return APIResult.newFailed("Invalid Multiple rules");
                }
            }
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid MultTransfer rules");
    }

    private APIResult CheckHashtimeblockTransfer(byte[] data, Transaction transaction) {
        HashtimeblockTransfer hashtimeblockTransfer = new HashtimeblockTransfer();
        if (hashtimeblockTransfer.RLPdeserialization(data)) {
            //amount
            if (hashtimeblockTransfer.getValue() < 0) return APIResult.newFailed("Value must be positive integer");

            //hashresult
            if (hashtimeblockTransfer.getHashresult().length != 32)
                return APIResult.newFailed("Wrong length of hashresult");
            //timestamp TODO 校验时间格式
            if (String.valueOf(hashtimeblockTransfer.getTimestamp()).length() != 10)
                return APIResult.newFailed("Timestamp is error");

            //查询事务
            Hashtimeblock hashtimeblock = new Hashtimeblock();
            Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(transaction.to);
            //Hashtimeblock是否存在
            if (!accountState.isPresent()) return APIResult.newFailed("Hashtimeblock do not exist");
            if (accountState.get().getType() != 3)
                return APIResult.newFailed("Must fill in the correct hashtimeblock");
            if (hashtimeblock.RLPdeserialization(accountState.get().getContract())) {
                //from 余额是否足够
                byte[] WDCbyte = new byte[20];
                //代币类型
                byte[] assetHash = hashtimeblock.getAssetHash();
                Optional<AccountState> accountStateFrom = wisdomRepository.getConfirmedAccountState(KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from));
                if (!accountStateFrom.isPresent()){
                    return APIResult.newFailed("From do not exist");
                }else {
                    if (accountStateFrom.get().getType() != 0)
                        return APIResult.newFailed("From must be Ordinary address");
                }

                //验证余额是否足够
                if (Arrays.equals(assetHash, WDCbyte)) {//WDC
                    if (accountStateFrom.get().getAccount().getBalance() < hashtimeblockTransfer.getValue())
                        return APIResult.newFailed("Insufficient funds");
                } else {//其他代币
                    if (accountStateFrom.get().getTokensMap().size() == 0)
                        return APIResult.newFailed("Insufficient funds");
                    if ((accountStateFrom.get().getTokensMap().get(assetHash) == null ? 0 : accountStateFrom.get().getTokensMap().get(assetHash)) < hashtimeblockTransfer.getValue())
                        return APIResult.newFailed("Insufficient funds");
                }
            } else {
                return APIResult.newFailed("Invalid hashtimeblock rules");
            }
            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid HashtimeblockTransfer rules");
    }

    private APIResult CheckHashheightblockTransfer(byte[] data, Transaction transaction) {
        HashheightblockTransfer hashheightblockTransfer = new HashheightblockTransfer();
        if (hashheightblockTransfer.RLPdeserialization(data)) {
            //amount
            if (hashheightblockTransfer.getValue() < 0) return APIResult.newFailed("Value must be positive integer");

            //hashresult
            if (hashheightblockTransfer.getHashresult().length != 32)
                return APIResult.newFailed("Wrong length of hashresult");
            //查询事务
            Hashheightblock hashheightblock = new Hashheightblock();
            Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(transaction.to);
            //Hashheightblock
            if (!accountState.isPresent()) return APIResult.newFailed("Hashheightblock do not exist");
            if (accountState.get().getType() != 4)
                return APIResult.newFailed("Must fill in the correct hashheightblock");
            if (hashheightblock.RLPdeserialization(accountState.get().getContract())) {
                //from 余额是否足够
                byte[] WDCbyte = new byte[20];
                //代币类型
                byte[] assetHash = hashheightblock.getAssetHash();
                Optional<AccountState> accountStateFrom = wisdomRepository.getConfirmedAccountState(KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from));
                if (!accountStateFrom.isPresent()){
                    return APIResult.newFailed("From do not exist");
                }else {
                    if (accountStateFrom.get().getType() != 0)
                        return APIResult.newFailed("From must be Ordinary address");
                }
                //验证余额是否足够
                if (Arrays.equals(assetHash, WDCbyte)) {//WDC
                    if (accountStateFrom.get().getAccount().getBalance() < hashheightblockTransfer.getValue())
                        return APIResult.newFailed("Insufficient funds");
                } else {//其他代币
                    if (accountStateFrom.get().getTokensMap().size() == 0)
                        return APIResult.newFailed("Insufficient funds");
                    if ((accountStateFrom.get().getTokensMap().get(assetHash) == null ? 0 : accountStateFrom.get().getTokensMap().get(assetHash)) < hashheightblockTransfer.getValue())
                        return APIResult.newFailed("Insufficient funds");
                }
            } else {
                return APIResult.newFailed("Invalid Hashheightblock rules");
            }


            return APIResult.newSuccess("SUCCESS");
        }
        return APIResult.newFailed("Invalid HashheightblockTransfer rules");
    }


    private APIResult CheckHashtimeblockGet(byte[] data, Transaction transaction) {
        HashtimeblockGet hashtimeblockGet = new HashtimeblockGet();
        if (hashtimeblockGet.RLPdeserialization(data)) {
            //transferhash 检验事务哈希是否存在
            Transaction hashtimeblockTransaction = wisdomBlockChain.getTransaction(hashtimeblockGet.getTransferhash());
            if (hashtimeblockTransaction == null)
                return APIResult.newFailed("Unable to get hashtimeblockTransfer transaction");
            if (!Arrays.equals(hashtimeblockTransaction.to,transaction.to))
                return APIResult.newFailed("To is different from payload txhash");
            HashtimeblockTransfer hashtimeblockTransfer = new HashtimeblockTransfer();
            if (hashtimeblockTransaction.payload[0] != 4)
                return APIResult.newFailed("Must fill in the correct hashtimeblockTransfer");
            //判断forkdb+db中是否有重复获取
            byte[] parenthash = wisdomRepository.getLatestConfirmed().getHash();
            if (repository.containsgetLockgetTransferAt(parenthash, hashtimeblockGet.getTransferhash()))
                return APIResult.newFailed("HashtimeblockGet had been exited");
            if (hashtimeblockTransfer.RLPdeserialization(ByteUtil.bytearraycopy(hashtimeblockTransaction.payload, 1, hashtimeblockTransaction.payload.length-1))) {
                if (hashtimeblockGet.getOrigintext().getBytes(StandardCharsets.UTF_8).length>512)
                    return APIResult.newFailed("Origintext Maximum exceeded");
                //判断哈希与原文哈希是否一致
                if (!Arrays.equals(SHA3Utility.sha3256(hashtimeblockGet.getOrigintext().getBytes(StandardCharsets.UTF_8)), hashtimeblockTransfer.getHashresult()))
                    return APIResult.newFailed("Origintext is wrong");
                //当前Unix时间
                Long nowTimestamp = System.currentTimeMillis() / 1000L;
                //判断时间
                if (hashtimeblockTransfer.getTimestamp() > nowTimestamp)
                    return APIResult.newFailed("The specified time is not reached");
                Hashtimeblock hashtimeblock = new Hashtimeblock();
                //Hashtimeblock
                Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(hashtimeblockTransaction.to);
                if (!accountState.isPresent()) return APIResult.newFailed("Hashtimeblock do not exist");
                if (hashtimeblock.RLPdeserialization(accountState.get().getContract())) {
                    if (!Arrays.equals(hashtimeblock.getPubkeyHash(), KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from)))
                        return APIResult.newFailed("From is different from the designated recipient");
                    return APIResult.newSuccess("SUCCESS");
                }
                return APIResult.newFailed("Invalid Hashtimeblock rules");
            }
            return APIResult.newFailed("Invalid HashtimeblockTransfer rules");
        }
        return APIResult.newFailed("Invalid HashtimeblockTransfer rules");
    }

    private APIResult CheckHashheightblockGet(byte[] data, Transaction transaction) {
        HashheightblockGet hashheightblockGet = new HashheightblockGet();
        if (hashheightblockGet.RLPdeserialization(data)) {
            //transferhash 检验事务哈希是否存在
            Transaction hashheightblockTransaction = wisdomBlockChain.getTransaction(hashheightblockGet.getTransferhash());
            if (hashheightblockTransaction == null)
                return APIResult.newFailed("Unable to get hashheightblockTransfer transaction");
            if (!Arrays.equals(hashheightblockTransaction.to,transaction.to))
                return APIResult.newFailed("To is different from payload txhash");
            if (hashheightblockTransaction.payload[0] != 6)
                return APIResult.newFailed("Must fill in the correct hashheightblockTransfer");
            //判断forkdb+db中是否有重复获取
            byte[] parenthash = wisdomRepository.getLatestConfirmed().getHash();
            if (repository.containsgetLockgetTransferAt(parenthash, hashheightblockGet.getTransferhash()))
                return APIResult.newFailed("HashheightblockGet had been exited");
            HashheightblockTransfer hashheightblockTransfer = new HashheightblockTransfer();
            if (hashheightblockTransfer.RLPdeserialization(ByteUtil.bytearraycopy(hashheightblockTransaction.payload, 1, hashheightblockTransaction.payload.length-1))) {
                try {
                    //判断哈希与原文哈希是否一致
                    if (!Arrays.equals(SHA3Utility.sha3256(Hex.decodeHex(hashheightblockGet.getOrigintext().toCharArray())), hashheightblockTransfer.getHashresult()))
                        return APIResult.newFailed("Origintext is wrong");
                    if (hashheightblockGet.getOrigintext().getBytes(StandardCharsets.UTF_8).length>512)
                        return APIResult.newFailed("Origintext Maximum exceeded");
                    //当前高度
                    Long nowHeight = wisdomBlockChain.getTopHeight();
                    //判断高度
                    if (hashheightblockTransfer.getHeight() > nowHeight)
                        return APIResult.newFailed("The specified height is not reached");
                } catch (DecoderException e) {
                    return APIResult.newFailed("Exception error");
                }

                Hashheightblock hashheightblock = new Hashheightblock();
                //Hashheightblock
                Optional<AccountState> accountState = wisdomRepository.getConfirmedAccountState(hashheightblockTransaction.to);
                if (!accountState.isPresent()) return APIResult.newFailed("Hashheightblock do not exist");
                if (hashheightblock.RLPdeserialization(accountState.get().getContract())) {
                    if (!Arrays.equals(hashheightblock.getPubkeyHash(), KeystoreAction.pubkeybyteToPubkeyhashbyte(transaction.from)))
                        return APIResult.newFailed("From is different from the designated recipient");
                    return APIResult.newSuccess("SUCCESS");
                }
                return APIResult.newFailed("Invalid Hashtimeblock rules");
            }
            return APIResult.newFailed("Invalid HashheightblockTransfer rules");
        }
        return APIResult.newFailed("Invalid HashheightblockTransfer rules");
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
                if (nowheight > 1241870) {
                    int blockcount = mul * configuration.getDay_count(nowheight);
                    if ((inheight + blockcount) > nowheight) {
//                        logger.info("nowheight: " + nowheight + "----> blockcount: " + blockcount + "----> inheight: " + inheight);
                        apiResult.setCode(5000);
                        apiResult.setMessage("In excess of the amount available");
                        return apiResult;
                    }
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
        boolean hasvote = wisdomBlockChain.containsPayload(Transaction.Type.EXIT_VOTE.ordinal(), payload);
        if (hasvote) {
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
        boolean hasmortgage = wisdomBlockChain.containsPayload(Transaction.Type.EXIT_MORTGAGE.ordinal(), payload);
        if (hasmortgage) {
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
}

