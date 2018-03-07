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
package com.pennanttech.pennapps.web.security.listener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.zkoss.zk.ui.Session;

/**
 * Used to initialize a session when it is created.
 *
 * <p>
 * How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface in
 * WEB-INF/zk.xml as a listener.</li>
 * <li>Then, even time ZK loader creates a new session, an instance of the
 * specified class is instantiated and {@link #init} is called.</li>
 * </ol>
 */
public class SessionInit implements org.zkoss.zk.ui.util.SessionInit {

	public SessionInit() {
		super();
	}
	
	/**
	 * Called when a session is created and initialized.
	 *
	 * <p>
	 * If the client is based on HTTP (such as browsers), you could retrieve the
	 * HTTP session by {@link Session#getNativeSession}
	 * </p>
	 *
	 * @param session
	 *            The session being created and initialized
	 * @param request
	 *            The request caused the session being created.
	 */
	@Override
	public void init(Session session, Object request) throws Exception {
		HttpSession httpSession = (HttpSession) session.getNativeSession();
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		/* Set the remote host. */
		String host;
		if (httpRequest.getHeader("X-FORWARDED-FOR") != null) {
			host = httpRequest.getHeader("X-FORWARDED-FOR");
		} else {
			host = httpRequest.getRemoteHost();
		}

		/* Set session attributes for remote host and random key. */
		httpSession.setAttribute("SATTR_REMOTE_HOST", host);
		httpSession.setAttribute("SATTR_RANDOM_KEY", RandomStringUtils.random(8, true, true));
	}
}
