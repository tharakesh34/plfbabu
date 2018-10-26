package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface QueryModuleRestService {

	@POST
	@Path("/queryService/updateQueryRequest")
	public WSReturnStatus updateQueryRequest(QueryDetail queryDetail) throws ServiceException;

}
