package org.wisdom.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WASMContract {
    // hash code of contract code
    // if the account contains none contract, contract hash will be null
    private byte[] contractHash;

    // root hash of contract db
    // if the account is not contract account, this field will be null
    private byte[] storageRoot;
}
