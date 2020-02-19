package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;

@WebService
public interface LoginSoapService {

	public LoginResponse userValidation(@WebParam(name = "loginRequest") LoginRequest loginRequest)
			throws ServiceException;

}