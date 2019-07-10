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

import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.CommandCode;
import org.wisdom.util.ByteUtil;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.util.ByteUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class SigScript {

    public static boolean checkoutTranStack(byte[] sig,byte[] pubkey,byte[] outscript,byte[] indexdate){
        //script
        List<Byte> scriptlist=new ArrayList<>();

        scriptlist.add(outscript[0]);
        scriptlist.add(outscript[1]);
        scriptlist.add(outscript[22]);
        scriptlist.add(outscript[23]);
        //public 160
        byte[] pubhash160= ByteUtil.bytearraycopy(outscript,2,20);
        Stack stack = new Stack();

        stack.push(sig);
        stack.push(pubkey);

        for(byte b:scriptlist){
            int codetype=b & 0xff;
            CommandCode type=CommandCode.fromInt(codetype);
            if(CommandCode.OP_DUP==type){
                byte[] copypubkey= (byte[]) stack.peek();
                stack.push(copypubkey);
            }else if(CommandCode.OP_HASH160==type){
                byte[] inpubkey=(byte[]) stack.pop();
                byte[] inpubhash=SHA3Utility.sha3256(inpubkey);
                byte[] inpub160=RipemdUtility.ripemd160(inpubhash);
                stack.push(inpub160);
                stack.push(pubhash160);
            }else if(CommandCode.OP_EQUALVERIFY==type){
                byte[] inpubhash160= (byte[]) stack.pop();
                byte[] outpubhash160=(byte[])stack.pop();
                if(Arrays.equals(inpubhash160,outpubhash160)){
                    stack.push(CommandCode.OP_TRUE);
                }else{
                    stack.push(CommandCode.OP_FALSE);
                }
                CommandCode commtype= (CommandCode) stack.pop();
                if(CommandCode.OP_FALSE==commtype){
                    return false;
                }
            }else if(CommandCode.OP_CHECKSIG==type){
                byte[] inpubkey= (byte[]) stack.pop();
                byte[] insig=(byte[]) stack.pop();
                Ed25519PublicKey ed25519PublicKey=new Ed25519PublicKey(inpubkey);
                return  ed25519PublicKey.verify(indexdate,insig);
            }else{
                return false;
            }
        }
        return true;
    }

    //Multisignature
    public static boolean checkoutsignatureStack(byte[] infirstbyte, byte[] redeemscript, List<byte[]> sigtextlist, byte[] outscript) {
        Stack stack = new Stack();
        int sigcount=0;
        while(infirstbyte.length>0){
            byte commpush=infirstbyte[0];
            if(commpush==0x04c){
                int len=ByteUtil.bytesInt(ByteUtil.bytearraycopy(infirstbyte,1,1));
                byte[] sig=ByteUtil.bytearraycopy(infirstbyte,2,len);
                stack.push(sig);
                sigcount++;
                infirstbyte=ByteUtil.bytearraycopy(infirstbyte,len+2,infirstbyte.length-len-2);
            }else if(commpush==0x4d){
                int len=ByteUtil.byte2Int(ByteUtil.bytearraycopy(infirstbyte,1,2));
                byte[] sig=ByteUtil.bytearraycopy(infirstbyte,3,len);
                stack.push(sig);
                sigcount++;
                infirstbyte=ByteUtil.bytearraycopy(infirstbyte,len+3,infirstbyte.length-len-3);
            }else{
                return false;
            }
        }
        byte[] redeemscript2=redeemscript;
        int needcount=redeemscript[0];
        int totalcount=0;
        int index=0;
        redeemscript=ByteUtil.bytearraycopy(redeemscript,1,redeemscript.length-1);
        while(redeemscript.length>0){
            byte commpush=redeemscript[0];
            if(commpush==0x04c){
                int len=ByteUtil.bytesInt(ByteUtil.bytearraycopy(redeemscript,1,1));
                byte[] sig=ByteUtil.bytearraycopy(redeemscript,2,len);
                stack.push(sig);
                index++;
                redeemscript=ByteUtil.bytearraycopy(redeemscript,len+2,redeemscript.length-len-2);
            }else if(commpush==0x4d){
                int len=ByteUtil.byte2Int(ByteUtil.bytearraycopy(redeemscript,1,2));
                byte[] sig=ByteUtil.bytearraycopy(redeemscript,3,len);
                stack.push(sig);
                index++;
                redeemscript=ByteUtil.bytearraycopy(redeemscript,len+3,redeemscript.length-len-3);
            }else{
                if(redeemscript.length==2){
                    totalcount=redeemscript[0];
                    stack.push(redeemscript[1]);
                    redeemscript=new byte[0];
                }else{
                    return false;
                }
            }
        }
        needcount=needcount & 0xff;
        needcount=CommandCode.fromOPnumber(needcount);
        totalcount=totalcount & 0xff;
        totalcount=CommandCode.fromOPnumber(totalcount);
        if(needcount<2 || totalcount<2 || totalcount<needcount || index!=totalcount || sigcount!=totalcount){
            return false;
        }
        byte comm= (byte) stack.pop();
        int codetype=comm & 0xff;
        CommandCode type=CommandCode.fromInt(codetype);
        int verifytrue=0;
        if(type==CommandCode.OP_CHECKMULTISIG){
            List<byte[]> publist=new ArrayList<>();
            List<byte[]> siglist=new ArrayList<>();
            for(int x=0;x<totalcount;x++){
                byte[] pubkey= (byte[]) stack.pop();
                publist.add(pubkey);
            }
            for(int x=0;x<totalcount;x++){
                byte[] pubkey= (byte[]) stack.pop();
                siglist.add(pubkey);
            }
            for(int x=0;x<totalcount;x++){
                //sig
                byte[] sig=siglist.get(x);
                //pubkey
                byte[] publickey=publist.get(x);
                //text
                byte[] indexdate=sigtextlist.get(x);
                Ed25519PublicKey ed25519PublicKey=new Ed25519PublicKey(publickey);
                if(ed25519PublicKey.verify(indexdate,sig)){
                    verifytrue++;
                    if(verifytrue==needcount){
                        break;
                    }
                }
            }
            if(verifytrue<needcount){
                return false;
            }
        }else{
            return false;
        }
        if(stack.size()!=0){
            return false;
        }
        //outscript 160
        List<Byte> commlist=new ArrayList<>();
        commlist.add(outscript[0]);
        commlist.add(outscript[21]);

        for(byte b:commlist){
            int outcomm=b & 0xff;
            CommandCode outtype=CommandCode.fromInt(outcomm);
            if(outtype==CommandCode.OP_HASH160){
                byte[] pub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(redeemscript2));
                byte[] outpub160=ByteUtil.bytearraycopy(outscript,1,20);
                stack.push(pub160);
                stack.push(outpub160);
            }else if(outtype==CommandCode.OP_EQUAL){
                byte[] outpub160= (byte[]) stack.pop();
                byte[] pub160=(byte[]) stack.pop();
                if(!Arrays.equals(outpub160,pub160)){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkoutHatcherStack(byte txtype, byte[] datascript, long utxoheight, long height) {
        Stack stack = new Stack();
        int days;
        if(txtype==0x09){
            days=120;
        }else{
            days=365;
        }
        if(datascript.length!=3){
            return false;
        }
        for(int x=0;x<datascript.length;x++){
            int datacomm=datascript[x] & 0xff;
            CommandCode datatype=CommandCode.fromInt(datacomm);
            if(datatype==CommandCode.OP_HEIGHT){
                stack.push(height);
            }else if(datatype==CommandCode.OP_FINALHEIGHT){
                long endheight=utxoheight+(days*5760);
                stack.push(endheight);
            }else if(datatype==CommandCode.OP_EQUAL){
                long endheight= (long) stack.pop();
                long nowheight=(long) stack.pop();
                if(nowheight<endheight){
                    return false;
                }
            }
        }
        return true;
    }
}