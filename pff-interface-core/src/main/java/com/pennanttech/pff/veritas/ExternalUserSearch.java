package com.pennanttech.pff.veritas;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pennanttech.pennapps.core.security.model.SecurityUser;
import com.pennanttech.pennapps.core.security.user.UserSearch;

public class ExternalUserSearch implements UserSearch {

	@Override
	public SecurityUser searchForUser(String username) throws UsernameNotFoundException {
		SecurityUser securityUser = new SecurityUser();
		securityUser.setLogin(username);
		securityUser.setFirstName("Manoj Kumar");
		securityUser.setMiddleName("");
		securityUser.setLastName("P");
		securityUser.setEmail("manojkumar.p@pennanttech.com");
		securityUser.setMobileNumber("+917416220988");

		return securityUser;
	}

}
