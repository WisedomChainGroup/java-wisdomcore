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

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.core.account.Account;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;

import java.math.BigDecimal;
import java.util.Arrays;

public class TransactionCheck {

    public static APIResult TransactionVerifyResult(byte[] transfer, WisdomBlockChain wisdomBlockChain, Configuration configuration, AccountDB accountDB, IncubatorDB incubatorDB, RateTable rateTable, long nowheight, boolean b, boolean state) {
        APIResult apiResult = new APIResult();
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
        tranlast = ByteUtil.bytearraycopy(tranlast, 32, tranlast.length - 32);
        //nownonce
        byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(frompubkey));
        long nownonce = accountDB.getNonce(frompubhash);
        if (state) {
            //todo:nonce判断不能大于最大15个
            long maxnonce = nownonce + 15;
            if (nownonce >= nonce || nonce > maxnonce) {
                apiResult.setCode(5000);
                apiResult.setMessage("Nonce is too small or Nonce too big. Over 15 Max");
                return apiResult;
            }
        } else {
            //todo:nonce为DB+1
            nownonce++;
            if (nownonce != nonce) {
                apiResult.setCode(5000);
                apiResult.setMessage("Nonce validates errors");
                return apiResult;
            }
        }
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
        if (b) {
            if ((gasPrice * gas) < configuration.getMin_procedurefee()) {
                apiResult.setCode(5000);
                apiResult.setMessage("Less than minimum handling charge");
                return apiResult;
            }
        }
        tranlast = ByteUtil.bytearraycopy(tranlast, 8, tranlast.length - 8);
        //amount
        byte[] amountbyte = ByteUtil.bytearraycopy(tranlast, 0, 8);
        long amount = ByteUtil.byteArrayToLong(amountbyte);
        if (type[0] == 0x03) {//存证
            if (amount != 0) {
                apiResult.setCode(5000);
                apiResult.setMessage("The amount of the deposit transaction is not 0");
                return apiResult;
            }
        }
        long nowbalance = accountDB.getBalance(frompubhash);
        if (type[0] == 0x01 || type[0] == 0x09) {
            if ((amount + gasPrice * gas) > nowbalance) {
                apiResult.setCode(5000);
                apiResult.setMessage("Not sufficient funds");
                return apiResult;
            }
        } else if (type[0] == 0x03 || type[0] == 0x0a || type[0] == 0x0b || type[0] == 0x0c || type[0] == 0x0d) {
            if (gasPrice * gas > nowbalance) {
                apiResult.setCode(5000);
                apiResult.setMessage("Not sufficient funds");
                return apiResult;
            }
        } else if (type[0] == 0x02) {//vote
            if ((amount + gasPrice * gas) > nowbalance) {
                apiResult.setCode(5000);
                apiResult.setMessage("Not sufficient funds");
                return apiResult;
            }
            //求余
            long remainder = (long) (amount % 100000000);
            if (remainder != 0) {
                apiResult.setCode(5000);
                apiResult.setMessage("The vote is not an integer");
                return apiResult;
            }
        }
        tranlast = ByteUtil.bytearraycopy(tranlast, 8, tranlast.length - 8);
        //sig
        byte[] sigdate = ByteUtil.bytearraycopy(tranlast, 0, 64);
        tranlast = ByteUtil.bytearraycopy(tranlast, 64, tranlast.length - 64);
        //topubkeyhash
        byte[] topubkeyhash = ByteUtil.bytearraycopy(tranlast, 0, 20);
        if (type[0] == 0x09 || type[0] == 0x0a || type[0] == 0x0b || type[0] == 0x0c) {
            if (!Arrays.equals(frompubhash, topubkeyhash)) {
                apiResult.setCode(5000);
                apiResult.setMessage("From and To are different");
                return apiResult;
            }
        }
        //fromaddress
        boolean verifyfrom = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyHashToAddress(frompubhash, (byte) 0x00)) == 0);
        if (!verifyfrom) {
            apiResult.setCode(5000);
            apiResult.setMessage("From format check error");
            return apiResult;
        }
        //toaddress
        if (type[0] != 0x03) {//非存证
            boolean verifyto = (KeystoreAction.verifyAddress(KeystoreAction.pubkeyHashToAddress(topubkeyhash, (byte) 0x00)) == 0);
            if (!verifyto) {
                apiResult.setCode(5000);
                apiResult.setMessage("To format check error");
                return apiResult;
            }
        }
        tranlast = ByteUtil.bytearraycopy(tranlast, 20, tranlast.length - 20);
        //bytelength
        byte[] date = ByteUtil.bytearraycopy(tranlast, 0, 4);
        int legnth = ByteUtil.byteArrayToInt(date);
        if (type[0] != 0x01 && type[0] != 0x02) {
            if (legnth == 0) {
                apiResult.setCode(5000);
                apiResult.setMessage("Payload cannot be empty");
                return apiResult;
            }
        }
        if (legnth > 0) {
            tranlast = ByteUtil.bytearraycopy(tranlast, 4, tranlast.length - 4);
            byte[] Payload = ByteUtil.bytearraycopy(tranlast, 0, legnth);
            APIResult result = PayloadCheck(Payload, type, amount, wisdomBlockChain, configuration, accountDB, incubatorDB, rateTable, nowheight, topubkeyhash);
            if (result.getCode() == 5000) {
                return result;
            }
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
        String key = Hex.encodeHexString(frompubkey) + nonce;
        apiResult.setData(key);
        return apiResult;
    }


    public static APIResult PayloadCheck(byte[] payload, byte[] type, long amount, WisdomBlockChain wisdomBlockChain, Configuration configuration, AccountDB accountDB, IncubatorDB incubatorDB, RateTable rateTable, long nowheight, byte[] topubkeyhash) {
        APIResult apiResult = new APIResult();
        try {
            if (type[0] == 0x09) {//孵化器
                //本金校验
                if (amount < 30000000000L) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Minimum incubation amount: 300wdc");
                    return apiResult;
                }
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(payload);
                int days = payloadproto.getType();
                if (days != 120 && days != 365) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Abnormal incubation days");
                    return apiResult;
                }
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
                long total = accountDB.getBalance(IncubatorAddress.resultpubhash());
                if (total < interest) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The incubation amount is paid out");
                    return apiResult;
                }
            } else if (type[0] == 0x0a || type[0] == 0x0b) {//提取利息、提取分享收益
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
                Incubator incubator = incubatorDB.selectIncubator(payload);
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
                if (type[0] == 0x0b) {//提取分享收益
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
                        int muls = (int) (nowincub % totalrate);
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
                    int blockcount = mul * configuration.getDay_count();

                    if ((inheight + blockcount) > nowheight) {
                        apiResult.setCode(5000);
                        apiResult.setMessage("In excess of the amount available");
                        return apiResult;
                    }
                }
            } else if (type[0] == 0x03) {//存证
                if (payload.length > 1000) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Memory cannot exceed 1000 bytes");
                    return apiResult;
                }
            } else if (type[0] == 0x0c) {//提取本金
                if (payload.length != 32) {//事务哈希
                    apiResult.setCode(5000);
                    apiResult.setMessage("Incubate transaction format errors");
                    return apiResult;
                }

                Incubator incubator = incubatorDB.selectIncubator(payload);
                if (incubator == null) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Unable to query incubation status");
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
            } else if (type[0] == 0x0d) {//撤回投票
                if (payload.length != 32) {//投票事务哈希
                    apiResult.setCode(5000);
                    apiResult.setMessage("The voting transaction payload was incorrectly formatted");
                    return apiResult;
                }
                boolean hasvote = accountDB.hasExitVote(payload);
                if (!hasvote) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("No voting business was withdrawn");
                    return apiResult;
                }
                Transaction transaction = wisdomBlockChain.getTransaction(payload);
                if (transaction == null) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Unable to get poll transaction");
                    return apiResult;
                }
                if (!Arrays.equals(transaction.to, topubkeyhash)) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("You have to withdraw your vote");
                    return apiResult;
                }
                if (transaction.amount != amount) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The number of votes is not correct");
                    return apiResult;
                }
                Account account = accountDB.selectaccount(topubkeyhash);
                if (account == null) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("Unable to withdraw");
                    return apiResult;
                }
                if (account.getVote() < amount) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The withdrawal amount is incorrect");
                    return apiResult;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        } catch (DecoderException e) {
            e.printStackTrace();
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
        apiResult.setCode(2000);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }

    public static boolean checkoutPool(Transaction t, WisdomBlockChain wisdomBlockChain, Configuration configuration, AccountDB accountDB, IncubatorDB incubatorDB, RateTable rateTable, long nowheight) {
        byte[] info = t.toRPCBytes();
        APIResult apiResult = TransactionCheck.TransactionVerifyResult(info, wisdomBlockChain, configuration, accountDB, incubatorDB, rateTable, nowheight, true, false);
        if (apiResult.getCode() == 5000) {
            return false;
        }
        return true;
    }
}
