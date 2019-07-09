package org.wisdom.core.validate;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.RateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 官方孵化余额校验，需要事务里填充高度
// 出块时校验，收到块时不校验
@Component
public class OfficialIncubateBalanceRule {

    @Autowired
    AccountDB accountDB;

    @Autowired
    RateTable rateTable;

    public List<Transaction> validateTransaction(List<Transaction> transaction) throws InvalidProtocolBufferException, DecoderException {
        List<Transaction> newlsit = new ArrayList<>();
        long totalincubate = accountDB.getBalance(IncubatorAddress.resultpubhash());
        for (Transaction tx : transaction) {
            if (tx.type == Transaction.Type.INCUBATE.ordinal()) {
                long height = tx.height;
                byte[] playload = tx.payload;
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(playload);
                int days = payloadproto.getType();
                String sharpub = payloadproto.getSharePubkeyHash();
                long share = 0;
                if (sharpub != null && sharpub != "") {
                    share = tx.getShare(height, rateTable, days);
                }
                long interest = tx.getInterest(height, rateTable, days);
                long total = share + interest;
                if (totalincubate < total) {
                    break;
                }
                totalincubate -= total;
            }

            newlsit.add(tx);
        }
        return newlsit;
    }
}
