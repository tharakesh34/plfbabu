package com.pennapps.core.encryption;

import com.pennanttech.pennapps.core.util.AES;

public class TestAES {
	public static void main(String[] args) {
		byte[] plainText = "V V S N MURTHY".getBytes();
		
		byte[] encryptedText = AES.encrypt(plainText);
		System.out.println(new String(encryptedText));
		
		System.out.println(AES.decrypt(encryptedText));
	}
}
