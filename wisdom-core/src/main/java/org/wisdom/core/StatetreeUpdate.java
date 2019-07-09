package org.wisdom.core;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.event.AccountUpdatedEvent;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.validate.MerkleRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class StatetreeUpdate implements ApplicationListener<NewBestBlockEvent> {
    @Autowired
    MerkleRule merkleRule;

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    ApplicationContext ctx;

    @Override
    public void onApplicationEvent(NewBestBlockEvent event) {
        Block b = event.getBlock();
        Map<String, Object> merklemap = null;
        try {
            merklemap = merkleRule.validateMerkle(b.body, b.nHeight);
            List<Account> accountList = (List<Account>) merklemap.get("account");
            List<Incubator> incubatorList = (List<Incubator>) merklemap.get("incubator");
            List<Object[]> accountobject = new ArrayList<>();
            List<Object[]> incubatorobjct = new ArrayList<>();
            for (Account account : accountList) {
                accountobject.add(new Object[]{
                        account.getId(),account.getBlockHeight(),account.getPubkeyHash(),account.getNonce()
                        ,account.getBalance(),account.getIncubatecost(),account.getMortgage()
                });
            }
            accountDB.insertAccountList(accountobject);
            for (Incubator incubator : incubatorList) {
                incubatorobjct.add(new Object[]{
                        incubator.getId(),incubator.getShare_pubkeyhash()
                        ,incubator.getPubkeyhash(),incubator.getTxid_issue()
                        ,incubator.getHeight(),incubator.getCost(),incubator.getInterest_amount()
                        ,incubator.getShare_amount(),incubator.getLast_blockheight_interest(),incubator.getLast_blockheight_share()
                });
            }
            incubatorDB.insertIncubatorList(incubatorobjct);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        } catch (DecoderException e) {
            e.printStackTrace();
            return;
        }
        ctx.publishEvent(new AccountUpdatedEvent(this, event.getBlock()));
    }
}
