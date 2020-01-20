package org.wisdom.db;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.store.Store;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.genesis.Genesis;

import java.util.Map;
import java.util.Set;

@Component
public class AccountStateTrie extends AbstractStateTrie<AccountState> {

    // mark whether the state of the block had been stored
    // block hash -> long
    private Store<byte[], Long> statusStore;

    // block hash -> state root

    private WisdomBlockChain bc;

    private AccountStateUpdater accountStateUpdater;

    @Setter
    private WisdomRepository repository;

    @Override
    protected String getPrefix() {
        return "account";
    }

    public AccountStateTrie(
            DatabaseStoreFactory factory,
            Block genesis,
            WisdomBlockChain bc,
            AccountStateUpdater accountStateUpdater
    ) throws Exception {
        super(AccountState.class, accountStateUpdater, genesis, factory, true, false);
        this.bc = bc;
        this.accountStateUpdater = accountStateUpdater;
        this.accountStateUpdater.setWisdomBlockChain(bc);
        this.accountStateUpdater.setRepository(repository);


//        for (long l : heights.keySet()) {
//            Trie<byte[], AccountState> trieTmp = stateTrie.revert(rootStore.get(heights.get(l)).get(), noDeleteStore);
//            List<Account> accounts = accountDB.getUpdatedAccounts(l);
//            for (Account account : accounts) {
//                AccountState state = trieTmp.get(account.getPubkeyHash()).get();
//
//                if (account.getBalance() != state.getAccount().getBalance()) {
//                    System.out.println("height = " + l);
//                    System.out.println("public key hash = " + HexBytes.encode(account.getPubkeyHash()));
//                    System.out.println("address = " + new PublicKeyHash(account.getPubkeyHash()).getAddress());
//                    System.out.println("expected " + account.getBalance());
//                    System.out.println("received " + state.getAccount().getBalance());
//                    throw new RuntimeException("invalid update operation");
//                }
//                if (account.getVote() != state.getAccount().getVote()) {
//                    System.out.println("height = " + l);
//                    System.out.println("public key hash = " + HexBytes.encode(account.getPubkeyHash()));
//                    System.out.println("address = " + new PublicKeyHash(account.getPubkeyHash()).getAddress());
//                    System.out.println("expected " + account.getVote());
//                    System.out.println("received " + state.getAccount().getVote());
//                    throw new RuntimeException("invalid vote update operation");
//                }
//                if (account.getMortgage() != state.getAccount().getMortgage()) {
//                    System.out.println("height = " + l);
//                    System.out.println("public key hash = " + HexBytes.encode(account.getPubkeyHash()));
//                    System.out.println("address = " + new PublicKeyHash(account.getPubkeyHash()).getAddress());
//                    System.out.println("expected " + account.getMortgage());
//                    System.out.println("received " + state.getAccount().getMortgage());
//                    throw new RuntimeException("invalid mortgage update operation");
//                }
//                List<Genesis.IncubateAmount> incubateAmountsList = genesisJSON.alloc.incubateAmount;
//                for (Genesis.IncubateAmount incubateAmount : incubateAmountsList) {
//                    if (Address.publicKeyHashToAddress(account.getPubkeyHash()).equals(incubateAmount.address)) {
//                        return;
//                    }
//                }
//                if (account.getIncubatecost() != state.getAccount().getIncubatecost()) {
//                    System.out.println("height = " + l);
//                    System.out.println("public key hash = " + HexBytes.encode(account.getPubkeyHash()));
//                    System.out.println("address = " + new PublicKeyHash(account.getPubkeyHash()).getAddress());
//                    System.out.println("expected " + account.getIncubatecost());
//                    System.out.println("received " + state.getAccount().getIncubatecost());
//                    throw new RuntimeException("invalid incubate cost update operation");
//                }
//                if (account.getNonce() != state.getAccount().getNonce()) {
//                    System.out.println("height = " + l);
//                    System.out.println("public key hash = " + HexBytes.encode(account.getPubkeyHash()));
//                    System.out.println("address = " + new PublicKeyHash(account.getPubkeyHash()).getAddress());
//                    System.out.println("expected " + account.getNonce());
//                    System.out.println("received " + state.getAccount().getNonce());
//                    throw new RuntimeException("invalid nonce update operation");
//                }
//
//                Incubator incubator = incubatorDB.selectIncubator(account.getPubkeyHash(), l);
//                if (incubator == null) {
//                    continue;
//                }
//                Transaction tx = bc.getTransaction(incubator.getTxid_issue());
//                HatchModel.Payload payloadProto = HatchModel.Payload.parseFrom(tx.payload);
//                int days = payloadProto.getType();
//                incubator.setDays(days);
//                Incubator interestIncubator = incubator.copy();
//                interestIncubator.setShare_pubkeyhash(null);
//                interestIncubator.setShare_amount(0);
//                interestIncubator.setLast_blockheight_share(0);
//                if (!state.getInterestMap().get(incubator.getTxid_issue()).equals(interestIncubator)) {
//                    throw new RuntimeException("invalid incubate cost update operation");
//                }
//                if (incubator.getShare_pubkeyhash() == null) {
//                    continue;
//                }
//                AccountState shareState = trieTmp.get(incubator.getShare_pubkeyhash()).get();
//                Incubator shareIncubator = incubator.copy();
//                shareIncubator.setPubkeyhash(null);
//                shareIncubator.setInterest_amount(0);
//                shareIncubator.setLast_blockheight_interest(0);
//                if (!shareState.getShareMap().get(incubator.getTxid_issue()).equals(shareIncubator)) {
//                    throw new RuntimeException("invalid share cost update operation");
//                }
//            }
//        }
    }
}
