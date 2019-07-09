package org.wisdom.encoding;


import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.List;

public interface CoreTypesDecoder {
    Block decodeBlock(byte[] data);


    Transaction decodeTransaction(byte[] data);

    long decodeBlockNumber(byte[] data);

    List<Transaction> decodeBlockBody(byte[] body);

    List<byte[]> decodeHashes(byte[] hashes);
}
