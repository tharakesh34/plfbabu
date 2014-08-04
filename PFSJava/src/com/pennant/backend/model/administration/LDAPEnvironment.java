/**
 * 
 */
package com.pennant.backend.model.administration;

/**
 * @author s051
 *
 */
public class LDAPEnvironment {
	
	
	private String contextFactory;  //java.naming.factory.initial's value
	private String authentication;  //authentication type like "simple" ,"SSL"
	private String principal;       //user name
	private String credentials;     //password
	private String providerURL;     //provide URL path 
	private String base;            //LDAP User Search path like ou='AJMAN' ,DC='AJMANBank',DC='com'
	
	public String getAuthentication() {
		return authentication;
	}
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getCredentials() {
		return credentials;
	}
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}
	public String getProviderURL() {
		return providerURL;
	}
	public void setContextFactory(String contextFactory) {
		this.contextFactory = contextFactory;
	}
	public String getContextFactory() {
		return contextFactory;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getBase() {
		return base;
	}

}
