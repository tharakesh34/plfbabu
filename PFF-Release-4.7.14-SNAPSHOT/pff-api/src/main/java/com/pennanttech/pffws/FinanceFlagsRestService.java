package com.pennanttech.pffws;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface FinanceFlagsRestService {
	@GET
	@Path("/flagService/getLoanFlags/{finReference}")
	public FinanceFlag getLoanFlags(@PathParam("finReference") String finReference) throws ServiceException;

	@POST
	@Path("/flagService/addLoanFlags")
	public WSReturnStatus addLoanFlags(FinanceFlag financeFlag) throws ServiceException;

	@DELETE
	@Path("/flagService/deleteLoanFlags")
	public WSReturnStatus deleteLoanFlags(FinanceFlag financeFlag) throws ServiceException;
}
