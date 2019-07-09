package org.wisdom.keystore.util;


import org.apache.commons.codec.binary.Base64;


public class Base64Encoder {


    /**
     * convert msg to base string
     *
     * @param msg plain string
     * @return base64 string
     */
    public static String encodeBase64(final String msg) {
        byte[] result = Base64.encodeBase64(msg.getBytes());
        return new String(result);
    }


    /**
     *
     * @param msg base64 string
     * @return plain string
     */
    public static String decodeBase64(String msg) {
        byte[] result= Base64.decodeBase64(msg);
        return new String(result);
    }


}
