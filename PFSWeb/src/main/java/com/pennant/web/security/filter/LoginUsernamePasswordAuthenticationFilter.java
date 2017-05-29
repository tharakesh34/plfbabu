package com.pennant.web.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pennant.crypto.AESCipher;

/**
 * Decrypt User Name and Password encrypted on form submission at
 * {@code encrypt.js}, called {@code AESCipher}
 */
public class LoginUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final static Logger logger = Logger.getLogger(LoginUsernamePasswordAuthenticationFilter.class);

	public LoginUsernamePasswordAuthenticationFilter() {
		super();
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		String strToCrypt = request.getParameter(getUsernameParameter());
		String token = (String) request.getSession().getAttribute("SATTR_RANDOM_KEY");

		try {
			return AESCipher.decrypt(strToCrypt, token);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return "";
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String strToCrypt = request.getParameter(getPasswordParameter());
		String token = (String) request.getSession().getAttribute("SATTR_RANDOM_KEY");

		try {
			return AESCipher.decrypt(strToCrypt, token);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return "";
	}
}
