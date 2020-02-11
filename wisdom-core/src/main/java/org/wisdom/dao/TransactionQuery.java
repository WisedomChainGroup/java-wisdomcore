package org.wisdom.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Data;
import org.tdf.common.util.HexBytes;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.core.account.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionQuery {
    private Integer offset;
    private Integer limit;
    private PublicKeyHash to;
    private HexBytes from;

    @JsonDeserialize(using = TypeDeserializer.class)
    private Integer type;

    public static class TypeDeserializer extends StdDeserializer<Integer>{
        public TypeDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            if(node.isNull()) return null;
            String s = node.asText();
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
    }

    @JsonIgnore
    public String getQuery(){
        if (to == null && from.isEmpty()) throw new RuntimeException("expect to or from");
        List<String> restrictions = new ArrayList<>();
        if(to != null) restrictions.add(" t.to = :to ");
        if(!from.isEmpty()) restrictions.add(" t.from = :from ");
        if(type != null) restrictions.add(" t.type = :type ");
        return " where " + String.join(" and ", restrictions);
    }
}
