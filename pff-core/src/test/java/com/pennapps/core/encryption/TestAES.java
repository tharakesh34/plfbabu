package com.pennapps.core.encryption;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.util.AES;

public class TestAES {
	@Test
	public void compare() {
		String input = "V V S N MURTHY";
		byte[] plainText = input.getBytes();
		byte[] encrypted = AES.encrypt(plainText);

		Assert.assertEquals(AES.decrypt(encrypted), input, "COMP");
	}
}
