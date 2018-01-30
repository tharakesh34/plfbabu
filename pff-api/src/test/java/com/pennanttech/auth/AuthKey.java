package com.pennanttech.auth;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.codec.Base64;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class AuthKey {
	private static Logger logger = Logger.getLogger(AuthKey.class);

	@Test(enabled = true)
	public void generate() throws UnsupportedEncodingException {
		// Usage: user:<user name>:<password>
		String authDetails = "user:admin:admin";
		String authKey = new String(Base64.encode(authDetails.getBytes()), "UTF-8");

		logger.info(authDetails);
		logger.info(authKey);
		Assert.assertNotNull(authKey);
	}

	@Test(enabled = true)
	public void crackDown() throws UnsupportedEncodingException {
		String authKey = "dXNlcjphZG1pbjpQZW5uYW50QDEyMw==";
		String authDetails = new String(Base64.decode(authKey.getBytes()), "UTF-8");

		logger.info(authKey);
		logger.info(authDetails);
		Assert.assertNotNull(authDetails);
	}
}
