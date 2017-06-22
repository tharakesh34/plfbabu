package com.pennanttech.framework.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pennant.crypto.AESCipher;

/**
 * Decrypt User Name and Password encrypted on form submission at {@code encrypt.js}, called {@code AESCipher}
 */
public class AuthenticationFormFilter extends UsernamePasswordAuthenticationFilter {
	private final static Logger	logger	= Logger.getLogger(AuthenticationFormFilter.class);

	public AuthenticationFormFilter() {
		super();
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		String username = request.getParameter(getUsernameParameter());
		String token = (String) request.getSession().getAttribute("SATTR_RANDOM_KEY");

		try {
			return AESCipher.decrypt(username, token);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return "";
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String password = request.getParameter(getPasswordParameter());
		String token = (String) request.getSession().getAttribute("SATTR_RANDOM_KEY");

		try {
			return AESCipher.decrypt(password, token);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return "";
	}
}
