package org.wisdom.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.wisdom.core.account.Transaction;

@Value
@AllArgsConstructor
@Builder
public class TransactionInfo{
    private Transaction transaction;
    private long height;
}
