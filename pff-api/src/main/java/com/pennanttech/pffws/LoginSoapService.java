package com.pennanttech.pffws;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;
import com.pennanttech.ws.model.login.UserRolesResponse;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface LoginSoapService {

	public LoginResponse userValidation(@WebParam(name = "loginRequest") LoginRequest loginRequest)
			throws ServiceException;

	public UserRolesResponse getUserRoles(@WebParam(name = "loginRequest") LoginRequest loginRequest)
			throws ServiceException;

}