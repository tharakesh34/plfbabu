package com.pennanttech.pennapps.core.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.pennanttech.pennapps.core.AppException;

/**
 * This class provides the functionality of a cryptographic cipher for
 * encryption and decryption with {@link AESCipher#ALGORITHM} transformation
 *
 */
public class AESCipherUtil {
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	private AESCipherUtil() {
		super();
	}

	/**
	 * Encrypt the message with the provided token
	 * 
	 * @param message
	 *            Message to encrypt.
	 * @param token
	 *            Data to digest
	 * @return The encrypted Message
	 */
	public static String encrypt(String message, String token) {

		if (message == null) {
			throw new IllegalArgumentException("message cannot be blank.");
		}

		if (token == null) {
			throw new IllegalArgumentException("token cannot be blank.");
		}

		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(DigestUtils.md5(token), "AES"),
					new IvParameterSpec(DigestUtils.md5(token)));
			return Base64.encodeBase64String(c.doFinal(message.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new AppException("");
		}
	}

	/**
	 * Decrypt the message with the provided token
	 * 
	 * @param message
	 *            Message to decrypt.
	 * @param token
	 *            Data to digest
	 * @return The encrypted Message
	 */
	public static String decrypt(String message, String token) {
		if (message == null) {
			throw new IllegalArgumentException("message cannot be blank.");
		}

		if (token == null) {
			throw new IllegalArgumentException("token cannot be blank.");
		}

		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(DigestUtils.md5(token), "AES"),
					new IvParameterSpec(DigestUtils.md5(token)));
			return new String(c.doFinal(Base64.decodeBase64(message)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new AppException("");
		}

	}
}
