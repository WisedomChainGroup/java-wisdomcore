package org.wisdom.keystore.crypto;

import com.kosprov.jargon2.api.Jargon2;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;


@Component
public class ArgonManage {
	public static enum Type {
		ARGON2d,
		ARGON2i,
		ARGON2id;

		Type(String value, String valueCapitalized) {
			this.value = value;
			this.valueCapitalized = valueCapitalized;
		}

		private String value = this.name().toLowerCase();
		private String valueCapitalized;

		Type() {
			this.valueCapitalized = Character.toUpperCase(this.value.charAt(0)) + this.value.substring(1);
		}

		public String getValue() {
			return this.value;
		}

		public String getValueCapitalized() {
			return this.valueCapitalized;
		}
	}

	private Jargon2.Type type;
	private byte[] salt;
	public static final int memoryCost = 20480;
	public static final int timeCost = 4;
	public static final int parallelism = 2;


	public ArgonManage() {
	}

	public ArgonManage(Type type) {
		this.type = Jargon2.Type.valueOf(type.name());
	}

	public ArgonManage(byte[] salt) {
		this.salt = salt;
	}

	public ArgonManage(Type type, byte[] salt) {
		this.type = Jargon2.Type.valueOf(type.name());
		this.salt = salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public byte[] hash(byte[] in){
		String password = Hex.encodeHexString(salt) + Hex.encodeHexString(in);
		return Jargon2.jargon2Hasher().type(this.type).memoryCost(memoryCost)
				.timeCost(timeCost).parallelism(parallelism).salt(salt)
				.password(password.getBytes()).rawHash();
	}

	public String kdf(){
		return this.type.name().toLowerCase();
	}

	public byte[] getSalt() {
		return salt;
	}
}
