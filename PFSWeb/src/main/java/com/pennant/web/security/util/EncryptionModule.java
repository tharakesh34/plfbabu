package com.pennant.web.security.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

public class EncryptionModule {
	private static final Logger logger = Logger.getLogger(EncryptionModule.class);

	public EncryptionModule(){
		super();
	}

	private static final String algorithm = "AES/CBC/PKCS5Padding";
	private static final byte[] keyValue = DigestUtils.md5("NTBjMjNlMGU4YWZkYjEzOTcxNGQ5M2NhYTBjNzA3ZTM5MmY3YmE3YWI3MGRkZjc4ZjBjZmZhZGQ2YzljY2EwYg==");
	private static final byte[] ivValue = DigestUtils.md5("NTBjMjNlMGU4YWZkYjEzOTcxNGQ5M2NhYTBjNzA3ZTM5MmY3YmE3YWI3MGRkZjc4ZjBjZmZhZGQ2YzljY2EwYg==");
	private static final IvParameterSpec ivspec = new IvParameterSpec(ivValue);
	private static final SecretKeySpec keyspec = new SecretKeySpec(keyValue, "AES");

	public static String encrypt(String plainText) {
		String cipherText = null;
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

			byte[] encVal = c.doFinal(plainText.getBytes());
			cipherText = Base64.encodeBase64String(encVal);
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}

		return cipherText;
	}

	public static String decrypt(String cipherText) {
		String plainText = null;
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] decordedValue = Base64.decodeBase64(cipherText);
			byte[] decValue = c.doFinal(decordedValue);
			plainText = new String(decValue);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return plainText;
	}

}
