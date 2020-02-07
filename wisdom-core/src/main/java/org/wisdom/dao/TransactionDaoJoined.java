package org.wisdom.dao;

import org.springframework.stereotype.Repository;
import org.wisdom.core.account.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class TransactionDaoJoined {
    private static final String QUERY_JOINS =
            "select new org.wisdom.core.account.Transaction(t.version, t.type, t.nonce, t.from, t.gasPrice, t.amount, t.payload, t.to, t.signature, ti.blockHash, h.height) " +
                    "from HeaderEntity h INNER JOIN TransactionIndexEntity ti on h.blockHash = ti.blockHash " +
                    "INNER JOIN TransactionEntity t on ti.txHash = t.txHash ";

    private EntityManager em;

    public TransactionDaoJoined(EntityManager em){
        this.em = em;
    }

    public List<Transaction> getTransactionsByBlockHash(byte[] param){
         Query query = em.createQuery(QUERY_JOINS +
                 "where ti.blockHash = :param order by ti.txIndex asc");
         query.setParameter("param", param);
         return query.getResultList();
    }


    public List<Transaction> getTransactionsByBlockHashIn(List<byte[]> params){
        Query query = em.createQuery(   QUERY_JOINS +
                "where ti.blockHash in :params order by h.height, ti.txIndex asc ");
        query.setParameter("params", params);
        return query.getResultList();
    }

    public Transaction getTransactionByTo(byte[] param){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.to = :param order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setMaxResults(1);
        List res = query.getResultList();
        return res.isEmpty() ? null : (Transaction) res.get(0);
    }

    public List<Transaction> getTransactionsByFrom(byte[] param, int offset, int limit){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.from = :param order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] param, int offset, int limit){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.from = :param and t.type = :type order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setParameter("type", type);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTo(byte[] param, int offset, int limit){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.to = :param order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTypeAndTo(int type, byte[] param, int offset, int limit){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.to = :param and t.type = :type order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setParameter("type", type);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByFromAndTo(byte[] fromParam, byte[] toParam, int offset, int limit){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.from = :fromParam and t.to = :toParam order by h.height, ti.txIndex asc");
        query.setParameter("fromParam", fromParam);
        query.setParameter("toParam", toParam);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] fromParam, byte[] toParam, int offset, int limit){
        Query query = em.createQuery(   QUERY_JOINS +
                "where t.from = :fromParam and t.to = :toParam and t.type = :type order by h.height, ti.txIndex asc");
        query.setParameter("fromParam", fromParam);
        query.setParameter("toParam", toParam);
        query.setParameter("type", type);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
