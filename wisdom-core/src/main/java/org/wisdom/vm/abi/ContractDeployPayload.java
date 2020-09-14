package org.wisdom.vm.abi;

import lombok.Data;

import java.util.List;

@Data
public class ContractDeployPayload {
    // 合约字节码
    private byte[] binary;
    // 构造器参数
    private Parameters parameters;
    // 合约 abi
    private List<ContractABI> contractABIs;
}
