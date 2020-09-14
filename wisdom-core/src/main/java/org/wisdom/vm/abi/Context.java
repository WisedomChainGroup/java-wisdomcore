package org.wisdom.vm.abi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;


@Getter
@AllArgsConstructor
public class Context {
    private byte[] parentHash;
    private long height;
    private long timestamp;

    private Transaction transaction;
    private AccountState contractAccount;
    private byte[] msgSender;
    private Uint256 amount;

    public boolean containsTransaction() {
        return transaction != null;
    }
}
