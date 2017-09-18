package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.systemDate.SystemDate;

@Produces("application/json")
public interface SystemDateRestService {

	@GET
	@Path("/sysDateService/getSystemDate")
	public SystemDate getSystemDate() throws ServiceException;
}
