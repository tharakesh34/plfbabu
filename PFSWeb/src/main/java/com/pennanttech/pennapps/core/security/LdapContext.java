/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.core.security;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * <p>
 * A LdapContext is used to search the user existence in Active Directory and
 * provide the user attributes
 * 
 */
public final class LdapContext {
	private static final Logger log = LogManager.getLogger(LdapContext.class);

	/*
	 * User not found in Active Directory
	 */
	public static final String LDAP64 = "LDAP64";
	/*
	 * Unable to connect Active Directory, please contact administrator
	 */
	public static final String LDAP80 = "LDAP80";

	/*
	 * Active Directory credentials not available
	 */
	public static final String LDAP81 = "LDAP81";

	@Value("${ldap.domain.url}")
	private String url;

	@Value("${ldap.domain.base}")
	private String base;

	@Value("${ldap.principal}")
	private String principal;

	@Value("${ldap.credentials}")
	private String credentials;

	private javax.naming.ldap.LdapContext context;

	public LdapContext() {
		super();
	}

	/**
	 * Returns user attributes map.
	 * 
	 * @param userName
	 *            The Name of the Active directory user to fetch the user
	 *            attributes
	 * @return <code>Map<String, String></code> Returns user attributes map
	 * @throws NamingException
	 */
	public Map<String, String> getUserDetail(String userName) throws NamingException {
		Map<String, String> details = new HashMap<>();
		try {
			Attributes attributes = getAttributes(userName);

			if (attributes != null) {
				setAttributes(details, attributes);
			}
		} catch (NamingException e) {
			log.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (context != null) {
				context.close();
			}
			context = null;
		}
		return details;
	}

	private void setAttributes(Map<String, String> details, Attributes attributes) {
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
			givenName = StringUtils
					.trimToNull(givenName.replace(UserAttributes.FIRST_NAME.getAttribute().concat(":"), ""));
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

	private void initializeContext() throws NamingException {
		log.info(Literal.ENTERING);

		try {
			Hashtable<String, String> env = new Hashtable<>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			env.put(Context.SECURITY_PRINCIPAL, this.principal);
			env.put(Context.SECURITY_CREDENTIALS, this.credentials);
			env.put(Context.PROVIDER_URL, this.url);

			log.info("Attempting to Connect to Active Directory...");

			context = new InitialLdapContext(env, null);
			log.info("Connection Successful.");

		} catch (Exception e) {
			log.warn(Literal.EXCEPTION, e);
		}
		log.info(Literal.LEAVING);
	}

	/**
	 * @param username
	 *            The Name of the Active directory user to search
	 * @return The non-null attributes in this search result. Can be empty.
	 * @throws NamingException
	 * @throws InterfaceException
	 */
	private Attributes getAttributes(String username) throws NamingException {
		if (context == null) {
			initializeContext();
		}

		if (StringUtils.trimToNull(base) == null || StringUtils.trimToNull(principal) == null
				|| StringUtils.trimToNull(credentials) == null) {
			throw new InterfaceException(LDAP81, "Active Directory credentials not available");
		}

		if (context == null) {
			throw new InterfaceException(LDAP80, "Unable to connect Active Directory, please contact administrator");
		}

		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

		constraints.setReturningAttributes(UserAttributes.getAttributes());

		NamingEnumeration<SearchResult> answer = context.search(base, "sAMAccountName=".concat(username), constraints);

		if (answer.hasMoreElements()) {
			return (answer.next()).getAttributes();
		} else {
			throw new InterfaceException(LDAP64, "User not found in Active Directory");
		}
	}

	private String getString(String attribute, Attributes attributes) {
		Object obj = attributes.get(attribute);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}
}