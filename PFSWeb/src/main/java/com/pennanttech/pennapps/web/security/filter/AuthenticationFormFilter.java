package com.pennanttech.pennapps.web.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.AESCipherUtil;

/**
 * Decrypt User Name and Password encrypted on form submission at
 * {@link encrypt.js}, called {@link AESCipherUtil}
 */
public class AuthenticationFormFilter extends UsernamePasswordAuthenticationFilter {
	private static final Logger log = LogManager.getLogger(AuthenticationFormFilter.class);

	public AuthenticationFormFilter() {
		super();
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		String username = request.getParameter(getUsernameParameter());
		String token = (String) request.getSession().getAttribute("SATTR_RANDOM_KEY");

		try {
			return AESCipherUtil.decrypt(username, token);
		} catch (Exception e) {
			log.error(Literal.EXCEPTION, e);
		}

		return "";
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String password = request.getParameter(getPasswordParameter());
		String token = (String) request.getSession().getAttribute("SATTR_RANDOM_KEY");

		try {
			return AESCipherUtil.decrypt(password, token);
		} catch (Exception e) {
			log.error(Literal.EXCEPTION, e);
		}

		return "";
	}
}
