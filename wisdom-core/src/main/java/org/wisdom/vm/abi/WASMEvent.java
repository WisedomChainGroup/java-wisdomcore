package org.wisdom.vm.abi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLPList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WASMEvent {
    private String name;
    private RLPList outputs;
}
