package com.pennanttech.framework.security.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailureHandler implements
		org.springframework.security.web.authentication.AuthenticationFailureHandler {
	public AuthenticationFailureHandler() {
		super();
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		response.sendRedirect("./loginDialog.zul?login_error=1");
	}
}
