package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;
import com.pennanttech.ws.model.login.UserRolesResponse;

@Produces("application/json")
public interface LoginRestService {

	@POST
	@Path("/loginservice/userValidate")
	public LoginResponse userValidation(LoginRequest loginRequest) throws ServiceException;

	@POST
	@Path("/loginservice/getUserRoles")
	public UserRolesResponse getUserRoles(LoginRequest loginRequest) throws ServiceException;

}
