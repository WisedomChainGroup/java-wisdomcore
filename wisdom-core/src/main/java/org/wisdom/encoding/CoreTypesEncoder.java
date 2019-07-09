package org.wisdom.encoding;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.List;

public interface CoreTypesEncoder {
    byte[] encodeBlock(Block block);

    byte[] encodeTransaction(Transaction transaction);

    byte[] encodeBlockNumber(long number);

    byte[] encodeBlockBody(List<Transaction> body);

    byte[] encodeHashes(List<byte[]> hashes);
}
