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