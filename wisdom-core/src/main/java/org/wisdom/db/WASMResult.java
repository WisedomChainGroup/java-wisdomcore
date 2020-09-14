package org.wisdom.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLPList;
import org.wisdom.vm.abi.WASMEvent;

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
}
