package com.pennant.api.user.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityUser;

@WebService
public interface SecurityUserSoapService {

	SecurityUser createSecurityUser(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus updateSecurityUser(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus addOperation(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus deleteOperation(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus enableUser(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus expireUser(@WebParam(name = "user") SecurityUser user);
}
