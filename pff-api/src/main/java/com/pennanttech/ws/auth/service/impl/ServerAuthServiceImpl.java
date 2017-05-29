package com.pennanttech.ws.auth.service.impl;

import com.pennanttech.ws.auth.dao.ServerAuthDAO;
import com.pennanttech.ws.auth.model.ServerAuthentication;
import com.pennanttech.ws.auth.service.ServerAuthService;
/**
 * Service implementation for methods that depends on <b>webServiceServerSecurity</b>.<br>
 * 
 */
public class ServerAuthServiceImpl implements  ServerAuthService{
private ServerAuthDAO serverAuthDAO;
/**
 * validateServer fetch the details by using WebServiceServerSecurityDAO's validateServer method.
 * @param tokenId (String)
 * @param  IPAddress (String)
 * 			    
 * @return WebServiceServerSecurity
 */
	public  ServerAuthentication validateServer(String tokenId,String IPAddress) {
		 return getServerAuthDAO().validateServer(tokenId,IPAddress);
	}



	// ******************************************************//
		// ****************** getter / setter *******************//
		// ******************************************************//
	
	
	public ServerAuthDAO getServerAuthDAO() {
		return serverAuthDAO;
	}
	public void setServerAuthDAO(ServerAuthDAO serverAuthDAO) {
		this.serverAuthDAO = serverAuthDAO;
	}

}
