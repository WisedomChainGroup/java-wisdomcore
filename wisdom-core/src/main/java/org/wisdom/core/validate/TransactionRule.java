package org.wisdom.core.validate;

import org.wisdom.core.account.Transaction;

public interface TransactionRule {
    Result validateTransaction(Transaction transaction);
}
