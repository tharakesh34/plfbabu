/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.web.security;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Simple strategy to match an <tt>HttpServletRequest</tt>.
 */
public class CsrfRequestMatcher implements RequestMatcher {
	private Pattern excludedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
	private Pattern excludedUrls = Pattern.compile("^(.*/zkau/web.*|.*/zkau/upload.*|.*/Charts.*)$");

	public CsrfRequestMatcher() {
		super();
	}
	
	/**
	 * Decides whether the rule implemented by the strategy matches the supplied
	 * request.
	 *
	 * @param request
	 *            the request to check for a match
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
