package org.wisdom.keystore.crypto;


import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.wisdom.keystore.util.ArrayUtils;
import org.wisdom.keystore.util.ArrayUtils;

import java.security.SecureRandom;


public class KeyPair{
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private static final SecureRandom RANDOM = new SecureRandom();

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public static KeyPair generateEd25519KeyPair(){
		Ed25519KeyPairGenerator kpg = new Ed25519KeyPairGenerator();
		kpg.init(new Ed25519KeyGenerationParameters(RANDOM));

		AsymmetricCipherKeyPair kp = kpg.generateKeyPair();
		KeyPair nkp = new KeyPair();
		Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters)kp.getPrivate();
		Ed25519PublicKeyParameters publicKey = (Ed25519PublicKeyParameters)kp.getPublic();
		nkp.publicKey = new PublicKey(publicKey.getEncoded());
		nkp.privateKey = new PrivateKey(privateKey.getEncoded());
		if (nkp.privateKey.isValid()){
			return nkp;
		}
		return  generateEd25519KeyPair();
	}



	public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public KeyPair(byte[] publicKey, byte[] privateKey) {
		this.publicKey = new PublicKey(publicKey);
		this.privateKey = new PrivateKey(privateKey);
	}

	public KeyPair() {
	}

	public void setBytes(byte[] k){

	}


	public byte[] getBytes(){
		return ArrayUtils.concat(this.privateKey.getBytes(), this.publicKey.getBytes());
	}
}
