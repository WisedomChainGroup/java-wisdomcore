package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.util.ByteArrayMap;
import org.wisdom.util.ByteArraySet;

import java.util.Map;
import java.util.Set;

@Component
public class AccountStateUpdater {
    private Map<byte[], AccountState> copy(Map<byte[], AccountState> accountStateMap) {
        Map<byte[], AccountState> res = new ByteArrayMap<>();
        for (Map.Entry<byte[], AccountState> entry : accountStateMap.entrySet()) {
            res.put(entry.getKey(), entry.getValue().copy());
        }
        return res;
    }

    public Map<byte[], AccountState> updateAll(Map<byte[], AccountState> accounts, Block block) {
        Map<byte[], AccountState> res = copy(accounts);
        for (Transaction tx : block.body) {
            getRelatedAccounts(tx).stream()
                    .map(res::get)
                    .peek(x -> {
                        if(x == null) throw new RuntimeException("unreachable here");
                    })
                    .forEach(x -> this.updateOne(tx, x));
        }
        return res;
    }

    public AccountState updateOne(Transaction transaction, AccountState accountState) {
        return null;
    }

    public Set<byte[]> getRelatedAccounts(Transaction transaction) {
        return new ByteArraySet();
    }

    public Set<byte[]> getRelatedAccounts(Block block) {
        Set<byte[]> ret = new ByteArraySet();
        block.body.stream().map(this::getRelatedAccounts)
                .forEach(ret::addAll);
        return ret;
    }
}
