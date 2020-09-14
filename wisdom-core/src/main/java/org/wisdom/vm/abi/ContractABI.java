package org.wisdom.vm.abi;

import com.fasterxml.jackson.databind.JsonNode;

public class ContractABI {
    private String name;
    // 0 = function 1 = event
    private int type;
    private int[] inputs;
    private int[] outputs;
}
