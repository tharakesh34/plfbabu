package com.pennanttech.auth;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class AuthKey {
	private static Logger logger = LogManager.getLogger(AuthKey.class);

	@Test(enabled = true)
	public void generate() throws UnsupportedEncodingException {
		// Usage: user:<user name>:<password>
		String authDetails = "user:admin:test";
		String authKey = Base64.getEncoder().encodeToString(authDetails.getBytes());

		logger.info(authDetails);
		logger.info(authKey);
		Assert.assertNotNull(authKey);
	}

	@Test(enabled = true)
	public void crackDown() throws UnsupportedEncodingException {
		String authKey = "dXNlcjphZG1pbjp0ZXN0";
		String authDetails = new String(Base64.getDecoder().decode(authKey));

		logger.info(authKey);
		logger.info(authDetails);
		Assert.assertNotNull(authDetails);
	}
}
