package org.wisdom.db;

import java.util.Set;

// 用于表示已确认的账户
public class ConfirmedAccounts {
    public byte[] blockHash;
    public Set<Account> accounts;
}
