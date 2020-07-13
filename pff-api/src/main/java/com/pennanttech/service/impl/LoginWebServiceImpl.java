package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.LoginController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.LoginRestService;
import com.pennanttech.pffws.LoginSoapService;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;
import com.pennanttech.ws.model.login.UserRolesResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class LoginWebServiceImpl implements LoginRestService, LoginSoapService {

	private final static Logger logger = Logger.getLogger(LoginWebServiceImpl.class);

	private LoginController loginController;

	@Override
	public LoginResponse userValidation(LoginRequest loginRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		LoginResponse response = null;
		response = loginController.validateLogin(loginRequest);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public UserRolesResponse getUserRoles(LoginRequest loginRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		UserRolesResponse response = new UserRolesResponse();
		if (StringUtils.isBlank(loginRequest.getUsrName())) {
			String[] valueParm = new String[1];
			valueParm[0] = "userName";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		response = loginController.getRoles(loginRequest);

		logger.debug(Literal.LEAVING);
		return response;
	}
	
	@Autowired
	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

}
