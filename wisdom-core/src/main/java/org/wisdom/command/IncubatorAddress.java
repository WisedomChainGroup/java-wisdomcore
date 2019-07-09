package org.wisdom.command;

import org.wisdom.keystore.wallet.KeystoreAction;

public class IncubatorAddress {

    private static String address = "1PpBHEx782C4VrtnQcJRTogn5UYmzCWAPH";

    public static byte[] resultpubhash() {
        return KeystoreAction.addressToPubkeyHash(address);
    }
}
