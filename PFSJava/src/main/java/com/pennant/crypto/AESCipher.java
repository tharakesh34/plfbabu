package com.pennant.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

public class AESCipher {
	private static final String algorithm = "AES/CBC/PKCS5Padding";

	public static String encrypt(String data, String token) throws Exception {
		Cipher c = Cipher.getInstance(algorithm);
		byte[] keyValue = DigestUtils.md5(token);
		byte[] ivValue = DigestUtils.md5(token);
		
		IvParameterSpec ivspec = new IvParameterSpec(ivValue);
		SecretKeySpec keyspec = new SecretKeySpec(keyValue, "AES");
		c.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

		byte[] encVal = c.doFinal(data.getBytes());
		String encryptedValue = Base64.encodeBase64String(encVal);

		return encryptedValue;
	}

	public static String decrypt(String encryptedData, String token) throws Exception {
		Cipher c = Cipher.getInstance(algorithm);
		byte[] keyValue = DigestUtils.md5(token);
		byte[] ivValue = DigestUtils.md5(token);
		
		IvParameterSpec ivspec = new IvParameterSpec(ivValue);
		SecretKeySpec keyspec = new SecretKeySpec(keyValue, "AES");
		c.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

		byte[] decordedValue = Base64.decodeBase64(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		
		return decryptedValue;
	}

	public static void main(String[] args) throws Exception {
		String password = "zxcvbn1$";
		 password = "pradeep";
		String token = RandomStringUtils.random(8, true, true);
		String passwordEnc = AESCipher.encrypt(password, token);
		String passwordDec = AESCipher.decrypt(passwordEnc, token);

		System.out.println("Plain Text : " + password);
		System.out.println("Encrypted Text : " + passwordEnc);
		System.out.println("Decrypted Text : " + passwordDec);
	}
}
