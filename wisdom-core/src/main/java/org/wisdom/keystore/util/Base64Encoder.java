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