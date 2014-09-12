/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  PasswordEncoderImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  30-07-2011	       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.sec.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.pennant.backend.model.administration.SecurityUser;

@SuppressWarnings("restriction")
public class PasswordEncoderImpl implements Serializable {
	private final static Logger logger = Logger.getLogger(PasswordEncoderImpl.class);
	private static final long serialVersionUID = -4639404994225504281L;

	public PasswordEncoderImpl() {
	}
	/**
	 * This method encrypt the rawPassword  with token and compares with encrypted Password .if encrypted password and 
	 *  raw Password are same returns true.
	 * @param encPass 
	 * @param rawPass
	 * @param token
	 * @return
	 */
	public boolean isPasswordValid(String encPass, String rawPass, Object token) {
		logger.debug("Entering ");
		boolean valid = false;
		String strToken = (String) token;
		try {
			byte[] decodedPassword = base64ToByte(encPass);
			byte[] tokenBytes      = base64ToByte(strToken);
			byte[] encodedPassword = getHash(rawPass, tokenBytes);
			valid = Arrays.equals(encodedPassword, decodedPassword);

		} catch (NoSuchAlgorithmException e) {
			logger.debug("error in isPasswordValid():NoSuchAlgorithmException"+e.toString());
		}
		catch (UnsupportedEncodingException e) {
			logger.debug("error in isPasswordValid():UnsupportedEncodingException"+e.toString());
		}
		catch (Exception e) {
			logger.debug("error in isPasswordValid()"+e.toString());
		} 
		logger.debug("Leaving ");
		return valid;
	}

	/**
	 * This method gets the password  from secUser Object ,encrypts the password with generated token and sets 
	 * Encrypted password and token to the  secUser Object and returns secUser object
	 * @param SecUser(secUser)

	 * @return SecUser
	 */

	public SecurityUser encodePassword(SecurityUser secUser) throws DataAccessException {
		logger.debug("Entering ");
		String password = secUser.getUsrPwd();

		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Salt generation 64 bits long
		byte[] bSalt = new byte[8];
		random.nextBytes(bSalt);
		// Digest computation
		byte[] bDigest = null;

		try {
			bDigest = getHash(password,bSalt);
		} catch (NoSuchAlgorithmException e) {
			logger.debug("error in encodePassword():NoSuchAlgorithmException"+e.toString());
		}
		catch (UnsupportedEncodingException e) {
			logger.debug("error in encodePassword():UnsupportedEncodingException"+e.toString());
		}
		secUser.setUsrPwd(byteToBase64(bDigest));
		secUser.setUsrToken(byteToBase64(bSalt));
		logger.debug("Leaving ");
		return secUser;
	}
	/**
	 * This method decodes the string using BASE64Decoder schemes
	 * @param data
	 * @return  byte[] 
	 * @throws IOException
	 */
    private byte[] base64ToByte(String data) throws IOException {
		logger.debug("Entering ");
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}
	/**
	 * This method performs encoding using 	BASE64Encoder schemes
	 * @param data
	 * @return String 
	 * @throws IOException
	 */
    private static String byteToBase64(byte[] data) {
		logger.debug("Entering ");
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}
	/**
	 * This method 
	 * @param  password
	 * @param  salt
	 * @return byte[] input 
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] getHash(String password, byte[] salt)
	throws NoSuchAlgorithmException, UnsupportedEncodingException{
		logger.debug("Entering ");
		final int ITERATION_NUMBER = 1000;

		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(salt);
		byte[] input = null;
		input = digest.digest(password.getBytes("UTF-8"));

		for (int i = 0; i < ITERATION_NUMBER; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		logger.debug("Leaving ");
		return input;

	}

}
