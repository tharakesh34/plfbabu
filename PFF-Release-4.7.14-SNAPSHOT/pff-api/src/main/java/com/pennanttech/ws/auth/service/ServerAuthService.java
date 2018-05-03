package com.pennanttech.ws.auth.service;

import com.pennanttech.ws.auth.model.ServerAuthentication;

/**
 * Service Declaration for methods that depends on <b>WebServiceServerSecurity</b>.<br>
 * 
 */
public interface ServerAuthService {
	 ServerAuthentication  validateServer(String tokenId,String IPAddress);
	
}
