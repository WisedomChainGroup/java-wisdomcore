package org.wisdom.vm.abi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLPList;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WASMResult {
    long gasUsed;
    RLPList returns;
    List<WASMEvent> WASMEvents;
    public static final WASMResult EMPTY = new WASMResult(0, RLPList.createEmpty(), Collections.emptyList());

    public static WASMResult empty(long gas){
        return new WASMResult(gas, RLPList.createEmpty(), Collections.emptyList());
    }

}
