package org.wisdom.Controller;

import org.wisdom.encoding.JSONEncodeDecoder;

public class ConsensusResult {
    private static JSONEncodeDecoder encodeDecoder = new JSONEncodeDecoder();
    public int code;
    public String message;

    public ConsensusResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static byte[] SUCCESS(String msg){
        return encodeDecoder.encode(new ConsensusResult(200, msg));
    }

    public static byte[] ERROR(String error){
        return encodeDecoder.encode(new ConsensusResult(400, error));
    }
}
