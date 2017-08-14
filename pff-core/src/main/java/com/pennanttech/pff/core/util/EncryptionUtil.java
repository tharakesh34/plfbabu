package com.pennanttech.pff.core.util;

import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptionUtil {
	static StandardPBEStringEncryptor encryptor = null;

	static {
		encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(System.getenv("APP_ENCRYPTION_PASSWORD"));
		encryptor.setAlgorithm("PBEWithMD5AndDES");
	}

	public static String encrypt(String message) {
		return encryptor.encrypt(message);
	}

	public static String decrypt(String encryptedMessage) {

		if (!StringUtils.startsWith(encryptedMessage, "ENC(")) {
			return encryptedMessage;
		}

		encryptedMessage = StringUtils.remove(encryptedMessage, "ENC(");

		encryptedMessage = StringUtils.removeEnd(encryptedMessage, ")");

		return encryptor.decrypt(encryptedMessage);
	}
}
