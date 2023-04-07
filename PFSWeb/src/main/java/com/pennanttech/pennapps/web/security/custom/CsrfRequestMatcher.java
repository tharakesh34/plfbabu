package com.pennanttech.pennapps.web.security.custom;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Simple strategy to match an <tt>HttpServletRequest</tt>.
 */
public class CsrfRequestMatcher implements RequestMatcher {
	private Pattern excludedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
	private Pattern excludedUrls = Pattern
			.compile("^(.*/zkau/web.*|.*/zkau/upload.*|.*/zkau/dropupload.*|.*/Charts.*|.*/saml.*|.*/zkau.*)$");

	public CsrfRequestMatcher() {
		super();
	}

	/**
	 * Decides whether the rule implemented by the strategy matches the supplied request.
	 *
	 * @param request the request to check for a match
	 * @return true if the request matches, false otherwise
	 */
	@Override
	public boolean matches(HttpServletRequest request) {
		if (excludedMethods.matcher(request.getMethod()).matches()) {
			return false;
		}

		return !excludedUrls.matcher(request.getRequestURL().toString()).matches();
	}
}
