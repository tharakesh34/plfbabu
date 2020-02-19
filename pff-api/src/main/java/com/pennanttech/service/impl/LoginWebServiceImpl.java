package com.pennanttech.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.LoginController;
import com.pennanttech.pffws.LoginRestService;
import com.pennanttech.pffws.LoginSoapService;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;

@Service
public class LoginWebServiceImpl implements LoginRestService, LoginSoapService {

	private final static Logger logger = Logger.getLogger(LoginWebServiceImpl.class);

	private LoginController loginController;

	@Override
	public LoginResponse userValidation(LoginRequest loginRequest) throws ServiceException {
		logger.debug("ENTERING");

		LoginResponse response = null;
		response = loginController.validateLogin(loginRequest);

		logger.debug("LEAVING");
		return response;
	}

	@Autowired
	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

}
