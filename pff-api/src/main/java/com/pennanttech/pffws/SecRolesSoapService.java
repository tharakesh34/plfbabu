package com.pennanttech.pffws;

import com.pennanttech.ws.model.secRoles.SecurityRoleDetail;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface SecRolesSoapService {
	@WebMethod
	public SecurityRoleDetail getSecRoles(@WebParam(name = "param") String param);
}
