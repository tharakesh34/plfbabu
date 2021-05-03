package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.finance.FinCovenantResponse;

@Produces("application/json")
public interface FinCovenantRestService {

	@POST
	@Path("/finCovenantService/addFinCovenant")
	WSReturnStatus addFinCovenant(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finCovenantService/updateFinCovenant")
	public WSReturnStatus updateFinCovenant(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finCovenantService/deleteFinCovenant")
	WSReturnStatus deleteFinCovenant(FinanceDetail financeDetail) throws ServiceException;

	@GET
	@Path("/finCovenantService/getFinCovenants/{finReference}")
	FinCovenantResponse getFinCovenants(@PathParam("finReference") String finReference) throws ServiceException;

}
