package org.wisdom.crypto.vrf;

import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.HashUtil;
import org.wisdom.crypto.PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.HashUtil;
import org.wisdom.crypto.PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519;

public class VRFPrivateKey {
    private PrivateKey signer;

    public VRFPrivateKey(String algorithm) throws CryptoException {
        if (algorithm.equals(Ed25519.getAlgorithm())){
            this.signer = Ed25519.GenerateKeyPair().getPrivateKey();
            return;
        }
        throw new CryptoException("unsupported signature policy");
    }

    /**
     *
     * @param seed random seed
     * @return verifiable random function result,
     * consists of a random variable, the seed and a proof for verifying
     * @throws CryptoException
     */
    public VRFResult rand(byte[] seed) throws CryptoException {
        if (seed.length > 255){
            throw new CryptoException("seed length overflow");
        }
        byte[] sig = signer.sign(seed);
        byte[] fin = HashUtil.sha256(sig);
        return new VRFResult(fin, sig);
    }

    public VRFPrivateKey(PrivateKey signer) {
        this.signer = signer;
    }
    public byte[] getEncoded(){
        return this.signer.getEncoded();
    }
    public VRFPublicKey generatePublicKey(){
        return new VRFPublicKey(this.signer.generatePublicKey());
    }
}
