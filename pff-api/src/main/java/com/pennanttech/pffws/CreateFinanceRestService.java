package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.financetype.FinanceInquiry;

@Produces("application/json")
public interface CreateFinanceRestService {

	@POST
	@Path("/finance/createFinance")
	FinanceDetail createFinance(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/createFinanceWithWIF")
	FinanceDetail createFinanceWithWIF(FinanceDetail financeDetail) throws ServiceException;

	@GET
	@Path("/finance/getFinanceDetails/{finReference}")
	FinanceDetail getFinanceDetails(@PathParam("finReference") String finReference) throws ServiceException;

	@GET
	@Path("/finance/getFinance/{finReference}")
	FinanceDetail getFinInquiryDetails(@PathParam("finReference") String finReference) throws ServiceException;

	@GET
	@Path("/finance/getFinanceWithCustomer/{cif}")
	FinanceInquiry getFinanceWithCustomer(@PathParam("cif") String custCif) throws ServiceException;

	@GET
	@Path("/finance/getFinanceWithCollateral/{collateralRef}")
	FinanceInquiry getFinanceWithCollateral(@PathParam("collateralRef") String collateralRef)
			throws ServiceException;

	@POST
	@Path("/finance/updateLoan")
	WSReturnStatus updateFinance(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/approveLoan")
	WSReturnStatus approveLoan(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/rejectLoan")
	WSReturnStatus rejectFinance(FinanceDetail financeDetail) throws ServiceException;
}
