/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.pennanttech.pennapps.core.AppException;

public class AES {
	private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec(
			new byte[] { 1, 4, 5, 5, 7, 8, 99, 1, 2, 11, 4, 2, 1, 3, 5, 2 });
	private static SecretKey secretKey = null;

	/**
	 * secret key,don't change this
	 */
	private static final String KEY = "NTBjMjNlMGU4YWZkYjEzOTcxNGQ5M2NhYTBjNzA3ZTM5MmY3YmE3YWI3MGRkZjc4ZjBjZmZhZGQ2YzljY2EwYg==";

	static {
		String hash = DigestUtils.sha256Hex(KEY);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			//
		}
		byte[] password = digest.digest(hash.getBytes());
		byte[] passwordKey128 = Arrays.copyOfRange(password, 0, 16);
		secretKey = new SecretKeySpec(passwordKey128, "AES");
	}

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException. If the constructor is used to create and initialize a new instance of the
	 *                                 declaring class by suppressing Java language access checking.
	 */
	private AES() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Encrypt the given string.
	 * 
	 * @param toCrypt The data to be encrypt.
	 * @return Encrypted string
	 */
	public static String encrypt(final String toCrypt) {
		try {
			return new String(encrypt(toCrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new AppException("AES", e);
		}
	}

	/**
	 * Encrypt the given bytes data.
	 * 
	 * @param toCrypt The data to be encrypt.
	 * @return Encrypted bytes data
	 */
	public static byte[] encrypt(final byte[] toCrypt) {
		Cipher cipher = null;
		byte[] encrypt = null;

		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, IV_PARAMETER_SPEC);
			encrypt = cipher.doFinal(toCrypt);
			encrypt = Base64.encodeBase64(encrypt);
		} catch (Exception e) {
			throw new AppException("AES", e);
		} finally {
			cipher = null;
		}
		return encrypt;
	}

	/**
	 * Decrypt the given string.
	 * 
	 * @param toDeCrypt The data to be decrypt.
	 * @return Decrypted string
	 */
	public static String decrypt(final String toDeCrypt) {
		try {
			return new String(decrypt(toDeCrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new AppException("AES", e);
		}
	}

	/**
	 * Decrypt the given bytes data.
	 * 
	 * @param toDeCrypt The data to be decrypt.
	 * @return Decrypted bytes data
	 */
	public static String decrypt(final byte[] toDeCrypt) {
		Cipher cipher = null;
		byte[] decrypt = null;

		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, IV_PARAMETER_SPEC);
			decrypt = Base64.decodeBase64(toDeCrypt);
			decrypt = cipher.doFinal(decrypt);
		} catch (Exception e) {
			throw new AppException("AES", e);
		} finally {
			cipher = null;
		}

		return new String(decrypt);
	}

	/**
	 * Converts the given string into hash
	 * 
	 * @param data The data to be hashed
	 * @return The hashed data.
	 */
	public static String getHash(String data) {
		return DigestUtils.sha256Hex(data);
	}
}
