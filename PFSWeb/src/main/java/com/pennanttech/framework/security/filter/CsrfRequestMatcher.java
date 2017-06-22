package com.pennanttech.framework.security.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class CsrfRequestMatcher implements RequestMatcher {
	private Pattern	excludedMethods	= Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
	private Pattern	excludedUrls	= Pattern.compile("^(.*/zkau/web.*|.*/zkau/upload.*|.*/Charts.*)$");

	public CsrfRequestMatcher() {
		super();
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (excludedMethods.matcher(request.getMethod()).matches()) {
			return false;
		}

		return !excludedUrls.matcher(request.getRequestURL().toString()).matches();
	}
}
