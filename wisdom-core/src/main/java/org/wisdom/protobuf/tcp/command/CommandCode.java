/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.wisdom.protobuf.tcp.command;

import java.util.HashMap;
import java.util.Map;

public enum CommandCode {

    OP_DUP(0x76),

    OP_PUSHDATA1(0x4c),

    OP_PUSHDATA2(0x4d),

    OP_TRUE(0x51),

    OP_2(0x52),

    OP_3(0x53),

    OP_4(0x54),

    OP_5(0x55),

    OP_6(0x56),

    OP_7(0x57),

    OP_8(0x58),

    OP_9(0x59),

    OP_10(0x5a),

    OP_11(0x5b),

    OP_12(0x5c),

    OP_13(0x5d),

    OP_14(0x5e),

    OP_15(0x5f),

    OP_16(0x60),

    OP_FALSE(0x00),

    OP_EQUAL(0x87),

    OP_EQUALVERIFY(0x88),

    OP_IF(0x63),

    OP_NOTIF(0x64),

    OP_ELSE(0x67),

    OP_ENDIF(0x68),

    OP_VERIFY(0x69),

    OP_RETURN(0x6a),

    OP_VOTE(0x6b),

    OP_SHA256(0xa8),

    OP_HASH160(0xa9),

    OP_SHA256ADDR(0xb9),

    OP_CHECKSIG(0xac),

    OP_CHECKMULTISIG(0xaf),

    OP_HEIGHT(0xc8),

    OP_FINALHEIGHT(0xc9),

    UNKNOWN(0xFF);

    private int reason;

    private static final Map<Integer, CommandCode> intToTypeMap = new HashMap<>();

    static {
        for (CommandCode type : CommandCode.values()) {
            intToTypeMap.put(type.reason, type);
        }
    }

    private CommandCode(int reason) {
        this.reason = reason;
    }

    public static CommandCode fromInt(int i) {
        CommandCode type = intToTypeMap.get(i);
        if (type == null)
            return CommandCode.UNKNOWN;
        return type;
    }

    public static int fromOPnumber(int i){
        CommandCode type = intToTypeMap.get(i);
        int result;
        switch (type){
            case OP_2:
                result=2;
                break;
            case OP_3:
                result=3;
                break;
            case OP_4:
                result=4;
                break;
            case OP_5:
                result=5;
                break;
            case OP_6:
                result=6;
                break;
            case OP_7:
                result=7;
                break;
            case OP_8:
                result=8;
                break;
            case OP_9:
                result=9;
                break;
            case OP_10:
                result=10;
                break;
            case OP_11:
                result=11;
                break;
            case OP_12:
                result=12;
                break;
            case OP_13:
                result=13;
                break;
            case OP_14:
                result=14;
                break;
            case OP_15:
                result=15;
                break;
            case OP_16:
                result=16;
                break;
            default:
                return 0;
        }
        return result;
    }

    public byte asByte() {
        return (byte) reason;
    }

    /*public static int getint(String types){
        for(int key:intToTypeMap.keySet()){
            String value=intToTypeMap.get(key).toString();
            if(value==types||value.equals(types)){
                return key;
            }
        }
        return 255;
    }*/

    public static void main(String args[]){
        for(Integer i:intToTypeMap.keySet()){
            System.out.println("key:"+i+"--->value:"+intToTypeMap.get(i));
        }
    }
}
