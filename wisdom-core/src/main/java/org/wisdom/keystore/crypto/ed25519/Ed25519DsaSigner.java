/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.keystore.crypto.ed25519;


import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.wisdom.keystore.crypto.CryptoException;
import org.wisdom.keystore.crypto.PrivateKey;
import org.wisdom.keystore.crypto.PublicKey;
import org.wisdom.keystore.crypto.PrivateKey;

public class Ed25519DsaSigner implements DsaSigner {
    private Ed25519PrivateKeyParameters prk;
    private Ed25519PublicKeyParameters puk;

    public Ed25519DsaSigner() {
    }

    public Ed25519DsaSigner(PrivateKey privateKey) {
        Ed25519PrivateKeyParameters prk = new Ed25519PrivateKeyParameters(
                privateKey.getBytes(), 0);
        this.prk = prk;
        this.puk = prk.generatePublicKey();
    }

    public Ed25519DsaSigner(PublicKey publicKey) {
        Ed25519PublicKeyParameters puk = new Ed25519PublicKeyParameters(
                publicKey.getBytes(), 0);
        this.puk = puk;
    }

    /**
     *
     * @param data The message to sign.
     * @return signature of the message.
     * @throws CryptoException
     */
    @Override
    public Signature sign(byte[] data) throws CryptoException {
        if (this.prk == null) {
            throw new CryptoException("failed to sign data, missing private key");
        }
        try{
            Signer signer = new Ed25519Signer();
            signer.init(true, this.prk);
            signer.update(data, 0, data.length);
            return new Signature(signer.generateSignature());
        }catch (Exception e){
            throw new CryptoException("failed to sign data");
        }
    }

    /**
     *
     * @param data The original message.
     * @param signature The generated signature.
     * @return validity of the signature
     * @throws CryptoException
     */
    @Override
    public boolean verify(byte[] data, Signature signature) throws CryptoException{
        if (this.puk == null) {
            throw new CryptoException("failed to sign data, missing public key");
        }
        try{
            Signer verifier = new Ed25519Signer();
            verifier.init(false, this.puk);
            verifier.update(data, 0, data.length);
            return verifier.verifySignature(signature.getBytes());
        }catch (Exception e){
            throw new CryptoException("failed to verify data");
        }
    }

    @Override
    public boolean isCanonicalSignature(Signature signature) {
        return false;
    }

    @Override
    public Signature makeSignatureCanonical(Signature signature) {
        return null;
    }
}