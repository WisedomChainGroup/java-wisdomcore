package org.wisdom.tools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.wallet.Keystore;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.util.Address;

import java.io.File;
import java.util.Scanner;

/**
 * KeyStoreTool
 * Usage:
 * <p>
 * 1. create keystore file:
 * .\gradlew runKeyStoreTool -PappArgs="create -o c:\Users\Sal\Desktop\key.json"
 * 2. load keystore file
 * .\gradlew runKeyStoreTool -PappArgs="load -f c:\Users\Sal\Desktop\key.json"
 */
public class KeyStoreTool {
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();

    private static final String CREATE = "create";

    private static final String LOAD = "load";

    public static void main(String[] args) throws Exception {
        CommandCreate commandCreate = new CommandCreate();
        CommandLoad commandLoad = new CommandLoad();
        JCommander jc = JCommander.newBuilder()
                .addCommand(CREATE, commandCreate)
                .addCommand(LOAD, commandLoad)
                .build();
        jc.parse(args);

        switch (jc.getParsedCommand()) {
            case CREATE: {
                String password = readPasswordFromStdinWithRepeat();
                createKeyStore(password, commandCreate.file);
                return;
            }
            case LOAD: {
                String password = readPasswordFromStdin();
                loadKeyStore(password, commandLoad.file);
                return;
            }
        }
        System.out.println("unknown command");
    }

    @Parameters(separators = "=", commandDescription = "create keystore file")
    private static class CommandCreate {

        @Parameter(names = {"-o", "--out"}, description = "the file to store encrypted private key")
        private String file;
    }

    @Parameters(separators = "=", commandDescription = "load from keystore file")
    private static class CommandLoad {

        @Parameter(names = {"-f", "--file"}, description = "the file to store encrypted private key")
        private String file;
    }

    private static String readPasswordFromStdinWithRepeat() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("please input your password");
            String password = scanner.nextLine();
            System.out.println("please repeat your password");
            if (scanner.nextLine().equals(password)) {
                return password;
            }
            System.err.println("your twice password input not match, please try");
        }
    }

    private static String readPasswordFromStdin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("please input your password");
        return scanner.nextLine();
    }

    private static void createKeyStore(String password, String file) throws Exception {
        Keystore keystore = Keystore.newInstance(password);
        File f = new File(file);
        if (f.exists()) {
            throw new RuntimeException("the file " + file + " has already exists");
        }
        FileUtils.writeByteArrayToFile(f, codec.encode(keystore));
    }

    private static void loadKeyStore(String password, String file) throws Exception {
        Keystore keystore = codec.decode(FileUtils.readFileToByteArray(new File(file)), Keystore.class);
        Ed25519PrivateKey privateKey = new Ed25519PrivateKey(KeystoreAction.decrypt(keystore, password));
        String addr = Address.publicKeyToAddress(privateKey.generatePublicKey().getEncoded());
        System.out.println("keystore loaded success");
        System.out.println("your public key is " + Hex.encodeHexString(privateKey.generatePublicKey().getEncoded()));
        System.out.println("your address is " + addr);
    }
}
