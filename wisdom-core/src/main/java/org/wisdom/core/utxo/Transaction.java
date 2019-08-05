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

package org.wisdom.core.utxo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.wisdom.crypto.HashUtil;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.Arrays;
import org.wisdom.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private byte version;
    private byte type;
    private byte[] hash;
    private long lockTime;
    private List<InPoint> inPoints;
    private List<OutPoint> outPoints;
    private byte[] storedHash;
    private long blockHeight;
    private byte[] blockHash;
    private long commission;

    public static Transaction generateCoinbase(String minerAddress, long height){
        return null;
    }

    @JsonIgnore
    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    @JsonIgnore
    public byte[] getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(byte[] blockHash) {
        this.blockHash = blockHash;
    }

    private byte[] getRaw() {
        byte[] bytes = new byte[]{};
        for (InPoint point : inPoints) {
            bytes = Arrays.concatenate(new byte[][]{
                    bytes, point.getPreviousTransactionHash(), BigEndian.encodeUint32(point.getOutPointIndex()),
                    point.getScript()
            });
        }
        for (OutPoint point : outPoints) {
            bytes = Arrays.concatenate(new byte[][]{
                    bytes, BigEndian.encodeUint32(point.getAmount()),
                    point.getScript()
            });
        }
        return Arrays.concatenate(new byte[][]{
                new byte[]{version}, BigEndian.encodeUint32(type), BigEndian.encodeUint32(lockTime),
                bytes
        });
    }

    /*public static Transaction fromProto(ProtocolModel.CommandMessage msg) {
        Transaction t = new Transaction();
        //手续费
        long fee=TransactionPoolState.getfee(new String(msg.getHash().toByteArray()));
        t.setCommission(fee);
        TransactionPoolState.delfee(new String(msg.getHash().toByteArray()));
        byte[] version = msg.getVersion().toByteArray();
        t.setVersion(version[0]);
        t.setType((byte) msg.getType().getNumber());
        t.setHash(msg.getHash().toByteArray());
        t.setLockTime(msg.getLockTime());
        List<InPoint> listin = decodeInpoints(msg.getInPoints().toByteArray(), t.getType());
        List<OutPoint> litsout = decodeOutPoints(msg.getOutPoints().toByteArray(), t.getType());
        t.setInPoints(listin);
        t.setOutPoints(litsout);
        return t;
    }*/

    /*public static ProtocolModel.CommandMessage encodeProtobuf(Transaction tx) {
        ProtocolModel.CommandMessage.Builder commmessage = ProtocolModel.CommandMessage.newBuilder();
        byte[] version = new byte[1];
        version[0] = tx.getVersion();
        commmessage.setVersion(ByteString.copyFrom(version));
        commmessage.setHash(ByteString.copyFrom(tx.getHash()));
        commmessage.setType(ProtocolModel.CommandMessage.Type.forNumber(tx.type));

        byte[] inpoints = encodeInpoints(tx.getInPoints());
        commmessage.setInPoints(ByteString.copyFrom(inpoints));
        byte[] outpoints = encodeOutPoints(tx.getOutPoints());
        commmessage.setOutPoints(ByteString.copyFrom(outpoints));
        long locktime = tx.getLockTime();
        commmessage.setLockTime(locktime);

        return commmessage.build();
    }*/

    public static byte[] encodeInpoints(List<InPoint> inPoints) {
        byte[] inpointsbyte = new byte[0];
        byte incount = (byte) inPoints.size();
        inpointsbyte = ByteUtil.prepend(inpointsbyte, incount);
        for (InPoint inPoint : inPoints) {
            byte[] hash = inPoint.getPreviousTransactionHash();
            byte[] index = ByteUtil.intToBytes(inPoint.getOutPointIndex());
            byte[] scriptlength = ByteUtil.intToBytes(inPoint.getScriptLength());
            byte[] inscript = inPoint.getScript();
            inpointsbyte = ByteUtil.merge(inpointsbyte, hash, index, scriptlength, inscript);
        }
        return inpointsbyte;
    }

    public static byte[] encodeOutPoints(List<OutPoint> outPoints) {
        byte[] outpointbyte = new byte[0];
        byte outcount = (byte) outPoints.size();
        outpointbyte = ByteUtil.prepend(outpointbyte, outcount);
        for (OutPoint outPoint : outPoints) {
            byte[] amount = ByteUtil.longToBytes(outPoint.getAmount());
            byte[] scriptlength = ByteUtil.intToBytes(outPoint.getScriptLength());
            byte[] outscript = outPoint.getScript();
            byte[] datalength=ByteUtil.intToBytes(outPoint.getScriptLength());
            byte[] datascript=outPoint.getDataScript();
            outpointbyte = ByteUtil.merge(outpointbyte, amount, scriptlength, outscript,datalength,datascript);
        }
        return outpointbyte;
    }

    public static List<InPoint> decodeInpoints(byte[] inPoints, byte type) {
        List<InPoint> listin = new ArrayList<>();
        int incount = inPoints[0];
        byte[] inlist = ByteUtil.bytearraycopy(inPoints, 1, inPoints.length - 1);
        for (int x = 0; x < incount; x++) {
            InPoint inPoint = new InPoint();
            byte[] befortransha = ByteUtil.bytearraycopy(inlist, 0, 32);
            inPoint.setPreviousTransactionHash(befortransha);
            byte[] beforindexbyte = ByteUtil.bytearraycopy(inlist, 32, 4);
            int beforindex = ByteUtil.byteArrayToInt(beforindexbyte);
            inPoint.setIntPointIndex(beforindex);
            byte[] inscriplength = ByteUtil.bytearraycopy(inlist, 36, 4);
            int insrciplength = ByteUtil.byteArrayToInt(inscriplength);
            int inscriptfull = befortransha.length + beforindexbyte.length + inscriplength.length;
            inPoint.setScriptLength(insrciplength);
            if (type == 0x00 || type == 0x01 || type == 0x03 || type == 0x02 || type == 0x06 || type == 0x04 || type == 0x05 || type == 0x09 || type == 0x0a || type == 0x0b || type == 0x0c) {
                byte[] inscript = ByteUtil.bytearraycopy(inlist, inscriptfull, insrciplength);
                inPoint.setScript(inscript);
                inscriptfull = inscriptfull + insrciplength;
                inlist = ByteUtil.bytearraycopy(inlist, inscriptfull, inlist.length - inscriptfull);
            }
            listin.add(inPoint);
        }

        return listin;
    }

    public static List<OutPoint> decodeOutPoints(byte[] outPoints, byte type) {
        /*List<OutPoint> listout = new ArrayList<>();
        int outcount = outPoints[0];
        byte[] outlist = ByteUtil.bytearraycopy(outPoints, 1, outPoints.length - 1);
        for (int x = 0; x < outcount; x++) {
            OutPoint outPoint = new OutPoint();
            outPoint.setIndex(x);
            byte[] brainbyte = ByteUtil.bytearraycopy(outlist, 0, 8);
            long brain = ByteUtil.byteArrayToLong(brainbyte);
            outPoint.setAmount(brain);
            byte[] outscrip = ByteUtil.bytearraycopy(outlist, 8, 4);
            int outscirptlength = ByteUtil.byteArrayToInt(outscrip);
            int outscriptfull = brainbyte.length + outscrip.length;
            outPoint.setScriptLength(outscirptlength);
            if (type == 0x00 || type == 0x01 || type == 0x04 || type == 0x05 || type == 0x06 || type == 0x03 || type == 0x02 || type == 0x09 || type == 0x0a || type == 0x0b || type == 0x0c) {
                byte[] outscript = ByteUtil.bytearraycopy(outlist, outscriptfull, outscirptlength);
                outPoint.setScript(outscript);
                if(brain>0){
                    //设置地址
                    String address=new TransactionCheck().ConversionsAddress(outscript,type);
                    if(address!=null){
                        outPoint.setAddress(address);
                    }
                }
                outscriptfull = outscriptfull + outscript.length;
                byte[] datalenbyte = ByteUtil.bytearraycopy(outlist, outscriptfull, 4);
                int datalen = ByteUtil.byteArrayToInt(datalenbyte);
                outPoint.setDataScriptLength(datalen);
                byte[] data = ByteUtil.bytearraycopy(outlist, outscriptfull + 4, datalen);
                outPoint.setDataScript(data);
                outscriptfull = outscriptfull + datalenbyte.length + datalen;
                outlist = ByteUtil.bytearraycopy(outlist, outscriptfull, outlist.length - outscriptfull);
            }
            listout.add(outPoint);
        }
        return listout;*/
        return null;
    }

    /*public static ProtocolModel.CommandMessage changeProtobuf(byte[] msg){
        ProtocolModel.CommandMessage.Builder commmessage = ProtocolModel.CommandMessage.newBuilder();
        //version
        byte version = msg[0];
        byte[] versionbyte = new byte[1];
        versionbyte[0] = version;
        commmessage.setVersion(ByteString.copyFrom(versionbyte));
        //sha3-256
        byte[] transha = ByteUtil.bytearraycopy(msg, 1, 32);
        commmessage.setHash(ByteString.copyFrom(transha));

        byte[] tranlast = ByteUtil.bytearraycopy(msg, 33, msg.length - 33);
        //type
        byte type = tranlast[0];
        commmessage.setType(ProtocolModel.CommandMessage.Type.forNumber(type));
        if (type == 0x00 || type == 0x01 || type == 0x03 || type == 0x02 || type == 0x06 || type== 0x09 || type== 0x0a || type==0x0b || type==0x0c) {
            //inpoint
            byte[] traninbyte = ByteUtil.bytearraycopy(tranlast, 1, tranlast.length - 1);
            //in count
            int incount = traninbyte[0];
            int inscriptlength = incount * 136 + 1;
            byte[] inscript = ByteUtil.bytearraycopy(traninbyte, 0, inscriptlength);

            commmessage.setInPoints(ByteString.copyFrom(inscript));
            byte[] lastout = ByteUtil.bytearraycopy(traninbyte, inscriptlength, traninbyte.length - inscriptlength);

            //outscript
            byte[] outscript = ByteUtil.bytearraycopy(lastout, 0, lastout.length - 4);
            commmessage.setOutPoints(ByteString.copyFrom(outscript));

            //locktime
            long locktime = ByteUtil.byteArrayToLong(ByteUtil.bytearraycopy(lastout, lastout.length - 4, 4));
            commmessage.setLockTime(locktime);

        } else if (type == 0x04 || type == 0x05) {//多签-多签、多签-普通
            //inpoint
            byte[] traninbyte = ByteUtil.bytearraycopy(tranlast, 1, tranlast.length - 1);
            int inscript = ByteUtil.byteArrayToInt(ByteUtil.bytearraycopy(traninbyte, 37, 4));
            int inlength = 41 + inscript;
            byte[] inscriptfull = ByteUtil.bytearraycopy(traninbyte, 0, inlength);

            commmessage.setInPoints(ByteString.copyFrom(inscriptfull));
            byte[] lastout = ByteUtil.bytearraycopy(traninbyte, inlength, traninbyte.length - inlength);

            //outscript
            byte[] outscript = ByteUtil.bytearraycopy(lastout, 0, lastout.length - 4);
            commmessage.setOutPoints(ByteString.copyFrom(outscript));

            //locktime
            long locktime = ByteUtil.byteArrayToLong(ByteUtil.bytearraycopy(lastout, lastout.length - 4, 4));
            commmessage.setLockTime(locktime);
        }
        return commmessage.build();
    }*/

/*    @JsonIgnore
    public byte[] getHash() {
        return HashUtil.keccak256(getRaw());
    }*/

    @JsonIgnore
    public byte[] getHash() {
        return HashUtil.keccak256(getRaw());
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    @JsonIgnore
    public byte[] getStoredHash() {
        return storedHash;
    }

    public void setStoredHash(byte[] storedHash) {
        this.storedHash = storedHash;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public List<InPoint> getInPoints() {
        return inPoints;
    }

    public void setInPoints(List<InPoint> inPoints) {
        this.inPoints = inPoints;
    }

    public List<OutPoint> getOutPoints() {
        return outPoints;
    }

    public void setOutPoints(List<OutPoint> outPoints) {
        this.outPoints = outPoints;
    }

    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public long getCommission() {
        return commission;
    }

    public void setCommission(long commission) {
        this.commission = commission;
    }

    @JsonIgnore
    public String getTransferOwner() {
        return null;
    }
}