package com.pennanttech.ws.auth.service;

import com.pennanttech.ws.auth.model.UserAuthentication;

/**
 * Service Declaration for methods that depends on <b>WebServiceServerSecurity</b>.<br>
 * 
 */
public interface UserAuthService {
	UserAuthentication validateSession(String tokenId);

	String createSession(UserAuthentication webServiceAuthanticastion);

	public void updateSession(UserAuthentication webServiceAuthanticastion);
}
