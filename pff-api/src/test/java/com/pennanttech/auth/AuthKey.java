package com.pennanttech.auth;

import java.io.UnsupportedEncodingException;

import org.springframework.security.crypto.codec.Base64;
import org.testng.annotations.Test;

@Test
public class AuthKey {
	@Test
	public void generate() throws UnsupportedEncodingException {
		// Usage: user:<user name>:<password>
		String authDetails = "user:maker:test";
		String authKey = new String(Base64.encode(authDetails.getBytes()), "UTF-8");

		System.out.println(authDetails);
		System.out.println(authKey);
	}

	@Test
	public void crackDown() throws UnsupportedEncodingException {
		String authKey = "dXNlcjp3cm06UGVubmFudEAxMjM=";
		String authDetails = new String(Base64.decode(authKey.getBytes()), "UTF-8");

		System.out.println(authKey);
		System.out.println(authDetails);
	}
}
