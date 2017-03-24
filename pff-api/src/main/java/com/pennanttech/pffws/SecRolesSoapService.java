package com.pennanttech.pffws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennanttech.ws.model.secRoles.SecurityRoleDetail;
@WebService
public interface SecRolesSoapService {
	@WebMethod
	public SecurityRoleDetail getSecRoles(@WebParam(name = "param") String param);
}
