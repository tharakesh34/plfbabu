package com.pennanttech.pennapps.core.security.ldap;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationProvider;

import com.pennanttech.pennapps.core.App;

public class ActiveDirectoryLdapAuthenticationProviderAdapter {
	private AuthenticationProvider ldapAuthenticationProvider;

	private static final Map<String, AuthenticationProvider> AUTHENTICATION_PROVIDERS = new HashMap<>();

	public ActiveDirectoryLdapAuthenticationProviderAdapter(AuthenticationProvider ldapAuthenticationProvider) {
		this.ldapAuthenticationProvider = ldapAuthenticationProvider;
	}

	public AuthenticationProvider getAuthenticationProvider(String domain) {
		if (domain == null || App.getProperty("ldap.domain.name").equals(domain)) {
			return ldapAuthenticationProvider;
		}
		return getAdditionalAuthenticationProvider(domain);

	}

	private AuthenticationProvider getAdditionalAuthenticationProvider(String domain) {
		AuthenticationProvider authenticationProvider = AUTHENTICATION_PROVIDERS.get(domain);

		if (authenticationProvider != null) {
			return authenticationProvider;
		}

		authenticationProvider = getAdditionalAuthenticationProvider(domain, domain.split("\\.")[0]);

		if (authenticationProvider != null) {
			AUTHENTICATION_PROVIDERS.put(domain, authenticationProvider);
		}

		return authenticationProvider;
	}

	private AuthenticationProvider getAdditionalAuthenticationProvider(String domain, String prefix) {
		String domainName = App.getProperty(domain);
		String url = App.getProperty(prefix + ".domain.url");

		AuthenticationProvider authenticationProvider = new ActiveDirectoryLdapAuthenticationProvider(domainName, url);
		return authenticationProvider;
	}

}
