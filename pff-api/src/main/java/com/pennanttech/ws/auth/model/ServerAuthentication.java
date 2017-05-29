package com.pennanttech.ws.auth.model;
/**
 * Model class for the <b>WebServiceServerSecurity table</b>.<br>
 *
 */
public class ServerAuthentication {
private String usrLogin;
private String tokenId;
private String ipAddress;

//******************************************************//
		// ****************** getter / setter *******************//
		// ******************************************************//

public String getUsrLogin() {
	return usrLogin;
}
public void setUsrLogin(String usrLogin) {
	this.usrLogin = usrLogin;
}

public String getIpAddress() {
	return ipAddress;
}
public void setIpAddress(String ipAddress) {
	this.ipAddress = ipAddress;
}
public String getTokenId() {
	return tokenId;
}
public void setTokenId(String tokenId) {
	this.tokenId = tokenId;
}
}
