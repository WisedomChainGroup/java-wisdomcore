package org.wisdom.db;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;

import java.util.HashMap;
import java.util.Map;

public class ValidatorStateTest {

    private Map<byte[], Long> map;

    @Before
    public void setUp() throws Exception {
        map = new HashMap<>();
        map.put(Hex.decode("1b83fceae112e4147e84886594bf2439a97ebb44"),0L);
        map.put(Hex.decode("8ab7d95c796cc90611109d518cad1b24b21b5114"),7L);
    }

    @Test
    public void test1() {
        ValidatorState state = new ValidatorState(map);
        byte[] encoded = RLPCodec.encode(state);
        ValidatorState state2 = RLPCodec.decode(encoded, ValidatorState.class);
        assert state.equals(state2);
    }

}
