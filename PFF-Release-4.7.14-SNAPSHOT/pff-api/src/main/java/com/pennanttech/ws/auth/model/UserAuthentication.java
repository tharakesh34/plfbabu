package com.pennanttech.ws.auth.model;

import java.sql.Timestamp;
/**
 * Model class for the <b>WebServiceUserSecurity table</b>.<br>
 *
 */
public class UserAuthentication {

	private String usrLogin;
	private String tokenId;
	private Timestamp expiry;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
		
	public String getUsrLogin() {
		return usrLogin;
	}
	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}
	
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	
	public Timestamp getExpiry() {
		return expiry;
	}
	public void setExpiry(Timestamp expiry) {
		this.expiry = expiry;
	}
}
