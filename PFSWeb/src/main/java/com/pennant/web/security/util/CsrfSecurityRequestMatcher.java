package com.pennant.web.security.util;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class CsrfSecurityRequestMatcher implements RequestMatcher {
	
	private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
	private Pattern allowedUrls = Pattern.compile("^(.*/zkau/web.*|.*/zkau/upload.*|.*/Charts.*)$");

	public CsrfSecurityRequestMatcher() {
		super();
	}
	
	@Override
	public boolean matches(HttpServletRequest request) {
				
		if (allowedMethods.matcher(request.getMethod()).matches()) {
			return false;
		}
	
		return !allowedUrls.matcher(request.getRequestURL().toString()).matches();
	}

}
