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
        String path = "E:/WisdomCore-J/wisdom-core/src/main/java/org/ethereum/protobuf/tcp";
        String out = "E:/WisdomCore-J/wisdom-core/src/main/java";
        String strCmd = "D:/protoc-3.7.0-win64/bin/protoc.exe -I=" + path + " --java_out=" + out + " " + path + "/" + protoFile;
        System.out.println(strCmd);
        Runtime.getRuntime().exec(strCmd);
        System.out.println("完成");

    }
}
