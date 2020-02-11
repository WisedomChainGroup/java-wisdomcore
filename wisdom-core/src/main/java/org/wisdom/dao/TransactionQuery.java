package org.wisdom.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.jackson.JsonComponent;
import org.tdf.common.util.HexBytes;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.core.account.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TransactionQuery {
    private Integer offset;
    private Integer limit;

    @Getter(AccessLevel.NONE)
    private String to;

    @Getter(AccessLevel.NONE)
    private String from;

    @Getter(AccessLevel.NONE)
    private String type;

    public Integer getType(){
        String s = type;
        s = s == null ? null : s.trim().toUpperCase();
        if(s == null || s.trim().isEmpty()){
            return null;
        }
        for (Transaction.Type t : Transaction.TYPES_TABLE) {
            if (t.toString().equals(s)) {
                return t.ordinal();
            }
        }
        try {
            int type = Integer.parseInt(s);
            if (type < 0 || type >= Transaction.Type.values().length) {
                throw new RuntimeException("not a type " + type);
            }
            return type;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getTo() {
        return to == null ?
                null :
                PublicKeyHash.fromHex(to).orElseThrow(IllegalArgumentException::new).getPublicKeyHash();
    }

    @SneakyThrows
    public byte[] getFrom() {
        return from == null ? null : Hex.decodeHex(from);
    }


    @JsonIgnore
    public Query getQuery(String joins, EntityManager entityManager){
        Map<String, Object> params = new HashMap<>();
        if (getTo() == null && getFrom() == null)
            throw new RuntimeException("expect to or from");
        List<String> restrictions = new ArrayList<>();
        if(getTo() != null) {
            restrictions.add(" t.to = :to ");
            params.put("to", getTo());
        }
        if(getFrom() != null) {
            restrictions.add(" t.from = :from ");
            params.put("from", getFrom());
        }
        if(getType() != null) {
            restrictions.add(" t.type = :type ");
            params.put("type", getType());
        }
        String suffix = " where " + String.join(" and ", restrictions);
        Query q  = entityManager.createQuery(joins + suffix);
        params.forEach(q::setParameter);
        if(offset != null) q.setFirstResult(offset);
        if(limit != null) q.setMaxResults(limit);
        return q;
    }
}
