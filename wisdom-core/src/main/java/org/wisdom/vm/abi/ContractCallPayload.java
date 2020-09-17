package org.wisdom.vm.abi;

import lombok.Data;

@Data
public class ContractCallPayload {
    private long gasLimit;
    private String method;
    private Parameters parameters;
}
