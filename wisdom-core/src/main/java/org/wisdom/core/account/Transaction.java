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

package org.wisdom.core.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.crypto.HashUtil;
import org.wisdom.encoding.BigEndian;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.ProtocolModel;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.Arrays;
import org.wisdom.util.ByteUtil;
import org.wisdom.core.incubator.RateTable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class Transaction {

    public static final int DEFAULT_TRANSACTION_VERSION = 1;

    public static final int PUBLIC_KEY_SIZE = 32;

    public static final int SIGNATURE_SIZE = 64;

    public static final int ADDRESS_SIZE = 20;

    public static final long[] GAS_TABLE = new long[]{
            0, 50000, 20000,
            100000, 50000, 50000,
            50000, 50000, 50000,
            100000, 100000, 100000,
            100000, 20000, 20000, 20000
    };

    public static final int TYPE_MAX = 16;

    public enum Type {
        COINBASE, TRANSFER, VOTE,
        DEPOSIT, TRANSFER_MULTISIG_MULTISIG, TRANSFER_MULTISIG_NORMAL,
        TRANSFER_NORMAL_MULTISIG, ASSET_DEFINE, ATOMIC_EXCHANGE,
        INCUBATE, EXTRACT_INTEREST, EXTRACT_SHARING_PROFIT,
        EXTRACT_COST, EXIT_VOTE, PLEDGE, EXIT_PLEDGE
    }

    public static Transaction createEmpty() {
        Transaction tx = new Transaction();
        tx.version = Transaction.DEFAULT_TRANSACTION_VERSION;
        tx.from = new byte[Transaction.PUBLIC_KEY_SIZE];
        tx.to = new byte[Transaction.ADDRESS_SIZE];
        tx.signature = new byte[Transaction.SIGNATURE_SIZE];
        return tx;
    }

    public static Transaction fromIncubateAmount(Genesis.UserIncubateAmount uia, int nonce) {
        Transaction tx = new Transaction();
        tx.type = Type.INCUBATE.ordinal();
        tx.nonce = nonce;
        tx.from = KeystoreAction.addressToPubkeyHash(uia.address);
        tx.gasPrice = 0;
        BigDecimal wdc = new BigDecimal(EconomicModel.WDC);
        tx.amount = uia.balance.multiply(wdc).longValue();
        tx.signature = new byte[Transaction.SIGNATURE_SIZE];
        tx.to = KeystoreAction.addressToPubkeyHash(uia.address);
        HatchModel.Payload.Builder payload = HatchModel.Payload.newBuilder();
        long interest = uia.interest.multiply(wdc).longValue();
        long share = uia.share.multiply(wdc).longValue();
        byte[] txid = ByteUtil.merge(ByteUtil.longToBytes(interest), ByteUtil.longToBytes(share));
        payload.setTxId(ByteString.copyFrom(txid));
        if (uia.shareAddress != null && uia.shareAddress != "") {
            payload.setSharePubkeyHash(Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(uia.shareAddress)));
        }
        payload.setType(uia.days);
        tx.payload = payload.build().toByteArray();
        return tx;
    }

    public static Transaction fromProto(ProtocolModel.Transaction tx) {
        Transaction res = new Transaction();
        res.version = tx.getVersion();
        res.type = tx.getType().getNumber();
        res.nonce = tx.getNonce();
        if (tx.getFrom() != null) {
            res.from = tx.getFrom().toByteArray();
        }
        res.gasPrice = tx.getGasPrice();
        res.amount = tx.getAmount();
        if (tx.getPayload() != null) {
            res.payload = tx.getPayload().toByteArray();
        }
        if (tx.getTo() != null) {
            res.to = tx.getTo().toByteArray();
        }
        if (tx.getSignature() != null) {
            res.signature = tx.getSignature().toByteArray();
        }
        return res;
    }

    // 防止 jackson 解析时报错
    private byte[] transactionHash;
    private int fee;
    private int days;

    public void setTransactionHash(byte[] transactionHash) {
        this.transactionHash = transactionHash;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public void setDays(int days) {
        this.days = days;
    }

    @JsonProperty("transactionHash")
    public byte[] getHash() {
        if (hashCache == null) {
            hashCache = HashUtil.keccak256(getRawForHash());
        }
        return hashCache;
    }

    public int version;

    @Min(0)
    @Max(TYPE_MAX)
    public int type;

    @Min(0)
    public long nonce;

    @NotNull
    @Size(min = PUBLIC_KEY_SIZE, max = PUBLIC_KEY_SIZE)
    public byte[] from;

    // unit brain
    @Min(0)
    public long gasPrice;

    @Min(0)
    public long amount;

    public byte[] payload;

    @NotNull
    @Size(min = ADDRESS_SIZE, max = ADDRESS_SIZE)
    public byte[] to;

    @NotNull
    @Size(max = SIGNATURE_SIZE, min = SIGNATURE_SIZE)
    public byte[] signature;

    @JsonIgnore
    private byte[] hashCache;

    @JsonIgnore
    private String hashHexString;

    public byte[] blockHash;

    @JsonProperty("blockHeight")
    public long height;

    public void setHashCache(byte[] hashCache) {
        this.hashCache = hashCache;
    }

    public byte[] getHashCache() {
        return hashCache;
    }

    @JsonIgnore
    private byte[] getRaw(boolean nullSignature) {
        long payloadLength = 0;
        if (payload != null) {
            payloadLength = payload.length;
        }
        byte[] sig = new byte[SIGNATURE_SIZE];
        if (!nullSignature) {
            sig = signature;
        }
        return Arrays.concatenate(new byte[][]{
                new byte[]{(byte) version}, // 1 byte
                new byte[]{(byte) type}, // 1 byte
                BigEndian.encodeUint64(nonce), // 8 byte
                from, // 32 byte
                BigEndian.encodeUint64(gasPrice), // 8 byte
                BigEndian.encodeUint64(amount), // 8 byte
                sig,
                to, // 20 byte
                BigEndian.encodeUint32(payloadLength),
                payload,
        });
    }

    @JsonIgnore
    public byte[] getRawForHash() {
        return getRaw(false);
    }

    @JsonIgnore
    public byte[] getRawForSign() {
        return getRaw(true);
    }

    public int size() {
        return getRawForHash().length + getHash().length;
    }

    @JsonIgnore
    public String getHashHexString() {
        if (hashHexString == null) {
            hashHexString = Hex.encodeHexString(getHash());
        }
        return hashHexString;
    }


    @JsonProperty("fee")
    public long getFee() {
        return gasPrice * GAS_TABLE[type];
    }

    @JsonIgnore
    public int getdays() {
        try {
            if (type == Type.INCUBATE.ordinal()) {
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(payload);
                return payloadproto.getType();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }


    public long getInterest(long height, RateTable rateTable, int days) {
        long interest = 0;
        if (type == Type.INCUBATE.ordinal()) {
            String rate = rateTable.selectrate(height, days);
            BigDecimal amountbig = BigDecimal.valueOf(amount);
            BigDecimal ratebig = new BigDecimal(rate);
            BigDecimal onemut = amountbig.multiply(ratebig);
            BigDecimal daysbig = BigDecimal.valueOf(days);
            interest = daysbig.multiply(onemut).longValue();
        }
        return interest;
    }

    public long getShare(long height, RateTable rateTable, int days) {
        if (type == Type.INCUBATE.ordinal()) {
            long rate = getInterest(height, rateTable, days);
            BigDecimal ratebig = BigDecimal.valueOf(rate);
            BigDecimal lvbig = BigDecimal.valueOf(0.1);
            return ratebig.multiply(lvbig).longValue();
        }
        return 0;
    }

    public ProtocolModel.Transaction encode() {
        ProtocolModel.Transaction.Builder builder = ProtocolModel.Transaction.newBuilder();
        builder.setVersion(version);
        builder.setType(ProtocolModel.Transaction.Type.forNumber(type));
        builder.setNonce(nonce);
        if (from != null) {
            builder.setFrom(ByteString.copyFrom(from));
        }
        builder.setGasPrice(gasPrice);
        builder.setAmount(amount);
        if (payload != null) {
            builder.setPayload(ByteString.copyFrom(payload));
        }
        if (to != null) {
            builder.setTo(ByteString.copyFrom(to));
        }
        if (signature != null) {
            builder.setSignature(ByteString.copyFrom(signature));
        }
        builder.setHash(ByteString.copyFrom(getHash()));
        return builder.build();
    }

    public byte[] toRPCBytes() {
        byte[] raw = getRawForHash();
        return Arrays.concatenate(new byte[]{(byte) version}, getHash(), Arrays.copyOfRange(raw, 1, raw.length));
    }

    public static Transaction transformByte(byte[] msg) {
        Transaction transaction = new Transaction();
        //version
        byte[] version = ByteUtil.bytearraycopy(msg, 0, 1);
        transaction.version = version[0];
        msg = ByteUtil.bytearraycopy(msg, 1, msg.length - 1);
        //hash
        byte[] hash = ByteUtil.bytearraycopy(msg, 0, 32);
        msg = ByteUtil.bytearraycopy(msg, 32, msg.length - 32);
        //type
        byte[] type = ByteUtil.bytearraycopy(msg, 0, 1);
        transaction.type = type[0];
        msg = ByteUtil.bytearraycopy(msg, 1, msg.length - 1);
        //nonce
        byte[] nonce = ByteUtil.bytearraycopy(msg, 0, 8);
        transaction.nonce = BigEndian.decodeUint64(nonce);
        msg = ByteUtil.bytearraycopy(msg, 8, msg.length - 8);
        //fromx
        byte[] from = ByteUtil.bytearraycopy(msg, 0, 32);
        transaction.from = from;
        msg = ByteUtil.bytearraycopy(msg, 32, msg.length - 32);
        //gasprice
        byte[] gasprice = ByteUtil.bytearraycopy(msg, 0, 8);
        transaction.gasPrice = BigEndian.decodeUint64(gasprice);
        msg = ByteUtil.bytearraycopy(msg, 8, msg.length - 8);
        //amount
        byte[] amount = ByteUtil.bytearraycopy(msg, 0, 8);
        transaction.amount = BigEndian.decodeUint64(amount);
        msg = ByteUtil.bytearraycopy(msg, 8, msg.length - 8);
        //sig
        byte[] sig = ByteUtil.bytearraycopy(msg, 0, 64);
        transaction.signature = sig;
        msg = ByteUtil.bytearraycopy(msg, 64, msg.length - 64);
        //to
        byte[] to = ByteUtil.bytearraycopy(msg, 0, 20);
        transaction.to = to;
        msg = ByteUtil.bytearraycopy(msg, 20, msg.length - 20);
        //payloadlen
        byte[] payloadlen = ByteUtil.bytearraycopy(msg, 0, 4);
        if (type[0] == 0x09 || type[0] == 0x0a || type[0] == 0x0b || type[0] == 0x0c || type[0] == 0x03 || type[0] == 0x0d) {//孵化器、提取利息、提取分享、提取本金、存证、撤回投票
            msg = ByteUtil.bytearraycopy(msg, 4, msg.length - 4);
            byte[] payload = ByteUtil.bytearraycopy(msg, 0, ByteUtil.byteArrayToInt(payloadlen));
            transaction.payload = payload;
        }
        return transaction;
    }

    public static ProtocolModel.Transaction changeProtobuf(byte[] msg) {
        ProtocolModel.Transaction.Builder tran = ProtocolModel.Transaction.newBuilder();
        //version
        byte[] version = ByteUtil.bytearraycopy(msg, 0, 1);
        tran.setVersion(version[0]);
        msg = ByteUtil.bytearraycopy(msg, 1, msg.length - 1);
        //hash
        byte[] hash = ByteUtil.bytearraycopy(msg, 0, 32);
        tran.setHash(ByteString.copyFrom(hash));
        msg = ByteUtil.bytearraycopy(msg, 32, msg.length - 32);
        //type
        byte[] type = ByteUtil.bytearraycopy(msg, 0, 1);
        tran.setType(ProtocolModel.Transaction.Type.forNumber(type[0]));
        msg = ByteUtil.bytearraycopy(msg, 1, msg.length - 1);
        //nonce
        byte[] nonce = ByteUtil.bytearraycopy(msg, 0, 8);
        tran.setNonce(BigEndian.decodeUint64(nonce));
        msg = ByteUtil.bytearraycopy(msg, 8, msg.length - 8);
        //fromx
        byte[] from = ByteUtil.bytearraycopy(msg, 0, 32);
        tran.setFrom(ByteString.copyFrom(from));
        msg = ByteUtil.bytearraycopy(msg, 32, msg.length - 32);
        //gasprice
        byte[] gasprice = ByteUtil.bytearraycopy(msg, 0, 8);
        tran.setGasPrice(BigEndian.decodeUint64(gasprice));
        msg = ByteUtil.bytearraycopy(msg, 8, msg.length - 8);
        //amount
        byte[] amount = ByteUtil.bytearraycopy(msg, 0, 8);
        tran.setAmount(BigEndian.decodeUint64(amount));
        msg = ByteUtil.bytearraycopy(msg, 8, msg.length - 8);
        //sig
        byte[] sig = ByteUtil.bytearraycopy(msg, 0, 64);
        tran.setSignature(ByteString.copyFrom(sig));
        msg = ByteUtil.bytearraycopy(msg, 64, msg.length - 64);
        //to
        byte[] to = ByteUtil.bytearraycopy(msg, 0, 20);
        tran.setTo(ByteString.copyFrom(to));
        msg = ByteUtil.bytearraycopy(msg, 20, msg.length - 20);
        //payloadlen
        byte[] payloadlen = ByteUtil.bytearraycopy(msg, 0, 4);
        tran.setPayloadlen(ByteUtil.byteArrayToInt(payloadlen));
        if (type[0] == 0x09 || type[0] == 0x0a || type[0] == 0x0b || type[0] == 0x0c || type[0] == 0x0d || type[0] == 0x03) {//孵化器、提取利息、提取分享、提取本金、撤回投票、存证
            msg = ByteUtil.bytearraycopy(msg, 4, msg.length - 4);
            byte[] payload = ByteUtil.bytearraycopy(msg, 0, ByteUtil.byteArrayToInt(payloadlen));
            tran.setPayload(ByteString.copyFrom(payload));
        }
        return tran.build();
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "    \"transactionHash\" : \"4ea9bf0a72af76cdc93d68e1205def4825108c855ae9a9fdb95593d79a58ecb8\",\n" +
                "    \"version\" : 1,\n" +
                "    \"type\" : 0,\n" +
                "    \"nonce\" : 2,\n" +
                "    \"from\" : \"0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"gasPrice\" : 0,\n" +
                "    \"amount\" : 2000200000,\n" +
                "    \"payload\" : null,\n" +
                "    \"to\" : \"552f6d4390367de2b05f4c9fc345eeaaf0750db9\",\n" +
                "    \"signature\" : \"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"blockHash\" : null,\n" +
                "    \"fee\" : 0,\n" +
                "    \"blockHeight\" : 0\n" +
                "  }";
        Transaction tx = new JSONEncodeDecoder().decodeTransaction(json.getBytes());
        System.out.println(tx.getHashHexString());
    }

}
