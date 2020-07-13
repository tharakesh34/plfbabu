package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;
import com.pennanttech.ws.model.login.UserRolesResponse;

@WebService
public interface LoginSoapService {

	public LoginResponse userValidation(@WebParam(name = "loginRequest") LoginRequest loginRequest)
			throws ServiceException;

	public UserRolesResponse getUserRoles(@WebParam(name = "loginRequest") LoginRequest loginRequest)
			throws ServiceException;

}