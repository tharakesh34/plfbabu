package com.pennant.api.user.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityUser;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface SecurityUserSoapService {

	SecurityUser createSecurityUser(@WebParam(name = "user") SecurityUser user);

	SecurityUser updateSecurityUser(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus addOperation(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus deleteOperation(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus enableUser(@WebParam(name = "user") SecurityUser user);

	WSReturnStatus expireUser(@WebParam(name = "user") SecurityUser user);
}
