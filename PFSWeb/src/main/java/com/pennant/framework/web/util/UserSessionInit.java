package com.pennant.framework.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.SessionInit;

public class UserSessionInit implements SessionInit{
	protected final Log logger = LogFactory.getLog(getClass());
	
	public UserSessionInit() {
		
	}
	
	@Override
	public void init(Session session, Object request) throws Exception {
		HttpSession httpSession = (HttpSession) session.getNativeSession();
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		httpSession.setAttribute("SATTR_REMOTE_HOST", httpRequest.getRemoteHost());
		
		httpSession.setAttribute("SATTR_RANDOM_KEY",  RandomStringUtils.random(8, true, true));

		if (logger.isDebugEnabled()) {
			logger.debug("New session: "+ (String.valueOf(httpSession.getId())));
			logger.debug("Remote host: " + httpRequest.getRemoteHost());
		}

	}

}
