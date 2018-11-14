package com.pennanttech.pff.veritas;

import com.pennanttech.pennapps.core.security.user.ExternalAuthenticationProvider;

public class UserAuthenticationProvider implements ExternalAuthenticationProvider {

	@Override
	public void authenticate(String userName, String password) {
		//throw new UsernameNotFoundException("user not found");
	}

}
