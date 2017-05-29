package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennanttech.ws.model.secRoles.SecurityRoleDetail;

@Produces("application/json")
public interface SecRolesRestService {
	
	@GET
	@Path("/secRoles/getSecRoles/{test}")
	SecurityRoleDetail getSecRoles(@PathParam("test") String test);
}
