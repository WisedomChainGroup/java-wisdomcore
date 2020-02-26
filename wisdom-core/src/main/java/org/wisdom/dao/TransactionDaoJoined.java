package org.wisdom.dao;

import lombok.NonNull;
import org.springframework.stereotype.Repository;
import org.wisdom.core.account.Transaction;
import org.wisdom.entity.HeaderEntity;
import org.wisdom.entity.TransactionEntity;
import org.wisdom.entity.TransactionIndexEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Repository
public class TransactionDaoJoined {
    private static final String QUERY_JOINS =
            "select new org.wisdom.core.account.Transaction(t.version, t.type, t.nonce, t.from, t.gasPrice, t.amount, t.payload, t.to, t.signature, ti.blockHash, h.height) " +
                    "from HeaderEntity h inner join TransactionIndexEntity ti on h.blockHash = ti.blockHash " +
                    "inner join TransactionEntity t on ti.txHash = t.txHash ";

    private static final String QUERYONE_JOINS = "from TransactionEntity t left join TransactionIndexEntity i on t.txHash = i.txHash " +
            "left join HeaderEntity h on h.blockHash = i.blockHash " +
            "where  h.height = :height and t.type = :type ";

    private EntityManager em;

    public TransactionDaoJoined(EntityManager em) {
        this.em = em;
    }

    public Optional<Transaction> getTransactionByHash(byte[] hash) {
        Query q = em.createQuery(QUERY_JOINS + " where ti.txHash = :param");
        q.setParameter("param", hash);
        List<Transaction> li = q.getResultList();
        return li.isEmpty() ? Optional.empty() : Optional.of(li.get(0));
    }

    public List<Transaction> getTransactionsByBlockHash(byte[] param) {
        Query query = em.createQuery(QUERY_JOINS +
                "where ti.blockHash = :param order by ti.txIndex asc");
        query.setParameter("param", param);
        return query.getResultList();
    }

    public List<Transaction> getTransactionByQuery(@NonNull TransactionQuery txQuery){
        return txQuery.getQuery(QUERY_JOINS, em).getResultList();
    }


    public List<Transaction> getTransactionsByBlockHashIn(List<byte[]> params) {
        Query query = em.createQuery(QUERY_JOINS +
                "where ti.blockHash in :params order by h.height, ti.txIndex asc ");
        query.setParameter("params", params);
        return query.getResultList();
    }

    public Transaction getTransactionByTo(byte[] param) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.to = :param order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setMaxResults(1);
        List res = query.getResultList();
        return res.isEmpty() ? null : (Transaction) res.get(0);
    }

    public List<Transaction> getTransactionsByFrom(byte[] param, int offset, int limit) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.from = :param order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] param, int offset, int limit) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.from = :param and t.type = :type order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setParameter("type", type);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTo(byte[] param, int offset, int limit) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.to = :param order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTypeAndTo(int type, byte[] param, int offset, int limit) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.to = :param and t.type = :type order by h.height, ti.txIndex asc");
        query.setParameter("param", param);
        query.setParameter("type", type);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByFromAndTo(byte[] fromParam, byte[] toParam, int offset, int limit) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.from = :fromParam and t.to = :toParam order by h.height, ti.txIndex asc");
        query.setParameter("fromParam", fromParam);
        query.setParameter("toParam", toParam);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] fromParam, byte[] toParam, int offset, int limit) {
        Query query = em.createQuery(QUERY_JOINS +
                "where t.from = :fromParam and t.to = :toParam and t.type = :type order by h.height, ti.txIndex asc");
        query.setParameter("fromParam", fromParam);
        query.setParameter("toParam", toParam);
        query.setParameter("type", type);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Map<String, Object>> getTransferByHeightAndTypeAndGas(long height, int type, long gas) {
        Query query = em.createQuery("select new map(t.txHash as tranHash, t.from as fromAddress, t.to as coinAddress, t.amount as amount, h.height as coinHeigth, t.gasPrice*:gas as fee)" +
                QUERYONE_JOINS);
        query.setParameter("gas", gas);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getHatchByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map(t.txHash as coinHash, t.to as coinAddress, t.amount as amount, h.height as blockHeight, t.payload as payload)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getInterestByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map(t.txHash as tranHash,t.to as coinAddress,t.amount as amount,h.height as coinHeigth,t.payload as coinHash)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getShareByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.txHash as coinHash,t.to as coinAddress,t.amount as amount,h.height as coinHeigth,r.txHash as tranHash,r.to as inviteAddress)" +
                "from TransactionEntity t " +
                "left join TransactionIndexEntity i on t.txHash=i.txHash " +
                "left join HeaderEntity h on h.blockHash=i.blockHash " +
                "left join TransactionEntity r on t.payload=r.txHash " +
                "where  h.height= :height and t.type= :type ");
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getCostByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.to as coinAddress,t.amount as amount,t.txHash as tranHash,h.height as coinHeigth,t.payload as tradeHash)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getVoteByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.to as toAddress,t.amount as amount,t.txHash as coinHash,h.height as coinHeigth,t.from as coinAddress)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getCancelVoteByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.to as toAddress,t.amount as amount,t.txHash as coinHash,h.height as coinHeigth,t.from as coinAddress,t.payload as tradeHash)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getMortgageByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.to as coinAddress,t.amount as amount,t.txHash as coinHash,h.height as coinHeigth)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getCancelMortgageByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.to as coinAddress,t.amount as amount,t.txHash as coinHash,h.height as coinHeigth,t.payload as tradeHash)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getCoinBaseByHeightAndType(long height) {
        Query query = em.createQuery("select new map (t.to as coinAddress,t.amount as amount,t.txHash as coinHash,h.height as coinHeigth,t.type as type )" +
                "from TransactionEntity t " +
                "left join TransactionIndexEntity i on t.txHash=i.txHash " +
                "left join HeaderEntity h on h.blockHash=i.blockHash " +
                "where h.height= :height");
        query.setParameter("height", height);
        return query.getResultList();
    }

    public List<Map<String, Object>> getAssetByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.txHash as coinHash,t.payload as tradeHash)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Map<String, Object>> getAssetTransferByHeightAndType(long height, int type) {
        Query query = em.createQuery("select new map (t.to as tohash,t.txHash as coinHash,h.height as coinHeigth,t.gasPrice as gasPrice,t.payload as tradeHash)" +
                QUERYONE_JOINS);
        query.setParameter("height", height);
        query.setParameter("type", type);
        return query.getResultList();
    }
}
