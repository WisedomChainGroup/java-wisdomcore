package org.wisdom.Controller;

public class ConsensuEntity {

    public static class Status {
        public long version;

        public long currentHeight;

        public byte[] bestBlockHash;

        public byte[] genesisHash;
    }
}
