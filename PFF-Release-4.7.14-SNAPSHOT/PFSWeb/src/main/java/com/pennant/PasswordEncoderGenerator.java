package com.pennant;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {
	public static void main(String[] args) {
		String password = "Pennant@123";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		System.out.println("Password : " +encodedPassword);
		System.out.println(passwordEncoder.matches("Pennant@123", encodedPassword));
		
	}
}
