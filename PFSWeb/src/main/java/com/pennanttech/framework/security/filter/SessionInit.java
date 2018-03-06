package com.pennanttech.framework.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Session;

public class SessionInit implements org.zkoss.zk.ui.util.SessionInit {
	private static final Logger	log	= LogManager.getLogger(SessionInit.class);
	
	public SessionInit() {
		super();
	}

	@Override
	public void init(Session session, Object request) throws Exception {
		HttpSession httpSession = (HttpSession) session.getNativeSession();
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// Get the remote host.
		String host;
		if (httpRequest.getHeader("X-FORWARDED-FOR") != null) {
			host = httpRequest.getHeader("X-FORWARDED-FOR");
		} else {
			host = httpRequest.getRemoteHost();
		}
		
		log.debug("Remote Host "+ host);

		// Set session attributes for remote host and random key.
		httpSession.setAttribute("SATTR_REMOTE_HOST", host);
		httpSession.setAttribute("SATTR_RANDOM_KEY", RandomStringUtils.random(8, true, true));
	}
}
