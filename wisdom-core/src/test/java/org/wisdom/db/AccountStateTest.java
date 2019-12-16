package org.wisdom.db;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.tdf.rlp.RLPCodec;
import org.wisdom.core.account.Account;

import java.util.Arrays;
import java.util.HashMap;

public class AccountStateTest {

    @Test
    public void Test1(){
        AccountState state = new AccountState();
        state.setAccount(new Account(1L, Hex.decode("1b83fceae112e4147e84886594bf2439a97ebb44"),
                1L,10000L,0L,0L,0L));
        state.setContract(new byte[]{0x01});
        state.setInterestMap(new HashMap<>());
        state.setShareMap(new HashMap<>());
        state.setTokensMap(new HashMap<>());
        state.setType(0);
        byte[] encoded = RLPCodec.encode(state);
        AccountState state2 = RLPCodec.decode(encoded, AccountState.class);
        assert state.getAccount().equals(state2.getAccount());
        assert Arrays.equals(state.getContract(), state2.getContract());
        assert state.getTokensMap().equals(state2.getTokensMap());
        assert state.getType() == state2.getType();
        assert state.getInterestMap().equals(state2.getInterestMap());
        assert state.getShareMap().equals(state2.getShareMap());
        assert state.getTokensMap().equals(state2.getTokensMap());
    }

}
