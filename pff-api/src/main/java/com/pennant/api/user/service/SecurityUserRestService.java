package com.pennant.api.user.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityUser;

import jakarta.jws.WebParam;

@Produces("application/json")
public interface SecurityUserRestService {

	@POST
	@Path("/securityUserService/createSecurityUser")
	public SecurityUser createSecurityUser(SecurityUser user);

	@POST
	@Path("/securityUserService/updateSecurityUser")
	public SecurityUser updateSecurityUser(SecurityUser user);

	@POST
	@Path("/securityUserService/addOperation")
	public WSReturnStatus addOperation(SecurityUser user);

	@POST
	@Path("/securityUserService/deleteOperation")
	public WSReturnStatus deleteOperation(SecurityUser user);

	@POST
	@Path("/securityUserService/enableUser")
	public WSReturnStatus enableUser(SecurityUser user);

	@POST
	@Path("/securityUserService/expireUser")
	public WSReturnStatus expireUser(SecurityUser user);

	@GET
	@Path("/securityUserService/getSecurityUser")
	public SecurityUser getSecurityUser(@WebParam(name = "securityUser") SecurityUser securityUser);
}