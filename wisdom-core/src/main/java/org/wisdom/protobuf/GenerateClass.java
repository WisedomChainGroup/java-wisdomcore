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

package org.wisdom.protobuf;

import com.google.protobuf.ByteString;

import java.io.IOException;

public class GenerateClass {

    public static void main(String[] args) throws IOException {

        /*
         *protobuf generation method
         *
         * */
        String protoFile = "Protocol.proto";
        String path = "C:/Users/Administrator/IdeaProjects/java-wisdomcore/wisdom-core/src/main/java/org/wisdom/protobuf/tcp";
        String out = "C:/Users/Administrator/IdeaProjects/java-wisdomcore/wisdom-core/src/main/java";
        String strCmd = "D:/protoc-3.7.0-win64/bin/protoc.exe -I=" + path + " --java_out=" + out + " " + path + "/" + protoFile;
        System.out.println(strCmd);
        Runtime.getRuntime().exec(strCmd);
        System.out.println("完成");

    }
}