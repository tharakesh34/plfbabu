package com.pennanttech.pffws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import com.pennanttech.ws.model.secRoles.SecurityRoleDetail;

@WebService
public interface SecRolesSoapService {
	@WebMethod
	public SecurityRoleDetail getSecRoles(@WebParam(name = "param") String param);
}
