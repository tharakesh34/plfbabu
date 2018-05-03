package com.pennanttech.ws.auth.service.impl;

import com.pennanttech.ws.auth.dao.UserAuthDAO;
import com.pennanttech.ws.auth.model.UserAuthentication;
import com.pennanttech.ws.auth.service.UserAuthService;
/**
 * Service implementation for methods that depends on <b>webServiceUserSecurity</b>.<br>
 * 
 */
public class UserAuthServiceImpl implements UserAuthService {
	
	private UserAuthDAO userAuthDAO;
	/**
	 * validateSession fetch the details by using WebServiceUserSecurityDAO's validateSession method.
	 * @param tokenId (String)
	 * @param  expiry (Timestamp)
	 * 			    
	 * @return WebServiceUserSecurity
	 */
	@Override
	public UserAuthentication validateSession(String tokenId) {
		return getUserAuthDAO().validateSession(tokenId);
	}

	/**
	 * createSession save the session details using WebServiceUserSecurityDAO's createSession method.
	 * @param webServiceAuthanticastion(WebServiceAuthanticastion)
	 * 			    
	 * @return TokenId
	 */

	@Override
	public String createSession(UserAuthentication webServiceAuthanticastion) {
	
		return getUserAuthDAO().createSession(webServiceAuthanticastion);
	}
	/**
	 * updateSession update the session details using WebServiceUserSecurityDAO's updateSession method.
	 * @param webServiceAuthanticastion(WebServiceAuthanticastion)
	 * 			    
	 * 
	 */
	@Override
	public void updateSession(
			UserAuthentication webServiceAuthanticastion) {
		getUserAuthDAO().updateSession(webServiceAuthanticastion);
		
	}

	

	// ******************************************************//
		// ****************** getter / setter *******************//
		// ******************************************************//

	public UserAuthDAO getUserAuthDAO() {
		return userAuthDAO;
	}

	public void setUserAuthDAO(UserAuthDAO userAuthDAO) {
		this.userAuthDAO = userAuthDAO;
	}


}
