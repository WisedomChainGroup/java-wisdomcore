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

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.wisdom.keystore.crypto.CryptoException;

import javax.crypto.KeyAgreement;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;


public class Curve25519ECDH implements ECDH{
    private static final String algorithm = "ECDH";
    private static final String format = "X.509";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private KeyPair keyPair;

    public Curve25519ECDH() throws CryptoException{
        try{
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator kpgen = KeyPairGenerator.getInstance("ECDH", "BC");
            X9ECParameters ecP = CustomNamedCurves.getByName("curve25519");
            ECParameterSpec ecSpec=new ECParameterSpec(ecP.getCurve(), ecP.getG(),
                    ecP.getN(), ecP.getH(), ecP.getSeed());
            kpgen.initialize(ecSpec);
            this.keyPair = kpgen.generateKeyPair();

        }catch (Exception e){
            throw new CryptoException("failed to generate ecdh instance");
        }

    }

    public Curve25519ECDH (KeyPair keyPair) {
        this.keyPair = keyPair;
    }


    @Override
    public byte[] generateSecretKey(PublicKey publicKey) throws CryptoException {
        try{
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
            keyAgreement.init(this.keyPair.getPrivate());
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret();
        }catch (Exception e){
            throw new CryptoException("failed to generate secret key");
        }
    }

    public byte[] generateSecretKey(byte[] publicKey) throws CryptoException {
        org.wisdom.keystore.crypto.PublicKey publicKey1 = new org.wisdom.keystore.crypto.PublicKey(publicKey, algorithm, format);
        return generateSecretKey(publicKey1);
    }

    public PublicKey getPublicKey(){
        return this.keyPair.getPublic();
    }

}