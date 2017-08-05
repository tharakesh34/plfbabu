package com.pennanttech.auth;

import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.Test;

@Test
public class AuthKey {
	@Test
	public void generate() {
		// Usage: user:<user name>:<password>
		String authDetails = "user:admin:Pennant@123";
		String authKey = Base64.encodeBase64String(authDetails.getBytes());

		System.out.println(authKey);
	}
}
