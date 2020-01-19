package org.wisdom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wisdom.core.account.Transaction;

import java.util.List;

public interface TransactionDaoJoined extends JpaRepository<Transaction, byte[]> {
    @Query("select new org.wisdom.core.account.Transaction(t.version, t.type, t.nonce, t.from, t.gasPrice, t.amount, t.payload, t.to, t.signature, ti.blockHash, h.height) " +
            "from HeaderEntity h INNER JOIN TransactionIndexEntity ti on h.blockHash = ti.blockHash " +
            "INNER JOIN TransactionEntity t on ti.txHash = t.txHash where ti.blockHash = :param"
    )
    List<Transaction> getTransactionsByBlockHash(byte[] param);

    @Query("select new org.wisdom.core.account.Transaction(t.version, t.type, t.nonce, t.from, t.gasPrice, t.amount, t.payload, t.to, t.signature, ti.blockHash, h.height) " +
            "from HeaderEntity h INNER JOIN TransactionIndexEntity ti on h.blockHash = ti.blockHash " +
            "INNER JOIN TransactionEntity t on ti.txHash = t.txHash where ti.blockHash in :params order by h.height asc "
    )
    List<Transaction> getTransactionsByBlockHashIn(List<byte[]> params);
}
