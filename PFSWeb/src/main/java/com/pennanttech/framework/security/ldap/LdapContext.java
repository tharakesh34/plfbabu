package com.pennanttech.framework.security.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

public class LdapContext {
	private final static Logger logger = Logger.getLogger(LdapContext.class);

	@Value("${ldap.domain.url}")
	private String url;

	@Value("${ldap.domain.base}")
	private String base;

	@Value("${ldap.principal}")
	private String principal;

	@Value("${ldap.credentials}")
	private String credentials;

	private javax.naming.ldap.LdapContext ldapContext;
	public static final String LDAP64 = "LDAP64";
	public static final String LDAP81 = "LDAP81";

	public LdapContext() {
		super();
	}

	public void initializeContext() throws NamingException {
		logger.info(Literal.ENTERING);

		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			env.put(Context.SECURITY_PRINCIPAL, this.principal);
			env.put(Context.SECURITY_CREDENTIALS, this.credentials);
			env.put(Context.PROVIDER_URL, this.url);

			logger.info("Attempting to Connect to Active Directory...");

			ldapContext = new InitialLdapContext(env, null);
			logger.info("Connection Successful.");

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.info(Literal.LEAVING);
	}
	
	public Map<String, String> getUserDetail(String userName) throws Exception {
		Map<String, String> details = new HashMap<>();

		try {
			Attributes attributes = getAttributes(userName);

			if (attributes != null) {
				
				String email = getString(UserAttributes.EMAIL.getAttribute(), attributes);
				if (email != null) {
					email = StringUtils.trimToNull(email.replace(UserAttributes.EMAIL.getAttribute().concat(":"), ""));
					details.put(UserAttributes.EMAIL.getAttribute(), email);
				}

				String mobile = getString(UserAttributes.MOBILE.getAttribute(), attributes);
				if (mobile != null) {
					mobile = StringUtils.trimToNull(mobile.replace(UserAttributes.MOBILE.getAttribute().concat(":"), ""));
					details.put(UserAttributes.MOBILE.getAttribute(), mobile);
				}

				String givenName = getString(UserAttributes.FIRST_NAME.getAttribute(), attributes);
				if (givenName != null) {
					givenName = StringUtils.trimToNull(givenName.replace(UserAttributes.FIRST_NAME.getAttribute().concat(":"), ""));
					details.put(UserAttributes.FIRST_NAME.getAttribute(), givenName);
				}

				String middleName = getString(UserAttributes.MIDDLE_NAME.getAttribute(), attributes);
				if (middleName != null) {
					middleName = StringUtils
							.trimToNull(middleName.replace(UserAttributes.MIDDLE_NAME.getAttribute().concat(":"), ""));
					details.put(UserAttributes.MIDDLE_NAME.getAttribute(), middleName);
				}

				String sn = getString(UserAttributes.LAST_NAME.getAttribute(), attributes);
				if (sn != null) {
					sn = StringUtils.trimToNull(sn.replace(UserAttributes.LAST_NAME.getAttribute().concat(":"), ""));
					details.put(UserAttributes.LAST_NAME.getAttribute(), sn);
				}
			}
			 
		} catch (Exception e) {
			throw e;
		} finally {
			if (ldapContext != null) {
				ldapContext.close();
			}
			ldapContext = null;
		}
		
		return details;
	}


	private Attributes getAttributes(String username) throws Exception {

		if (ldapContext == null) {
			initializeContext();
		}
		
		if (StringUtils.trimToNull(base) == null || StringUtils.trimToNull(principal) == null
				|| StringUtils.trimToNull(credentials) == null) {
			throw  new InterfaceException("LDAP81","Active Directory credentials not available");
		}
		
		if (ldapContext == null) {
			throw  new InterfaceException("LDAP80","Unable to connect Active Directory, please contact administrator");
		}

		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

		constraints.setReturningAttributes(UserAttributes.getAttributes());

		NamingEnumeration<SearchResult> answer = ldapContext.search(base, "sAMAccountName=".concat(username), constraints);

		if (answer.hasMoreElements()) {
			return ((SearchResult) answer.next()).getAttributes();
		} else {
			String[] parameters = new String[1];
			parameters[0] = username;
			throw  new InterfaceException(LDAP64, ErrorUtil.getErrorDetail(new ErrorDetails("LDAP64", parameters)).getError());
		}
	}
	
	private String getString(String attribute, Attributes attributes) throws Exception {
		Object obj = attributes.get(attribute);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}
}