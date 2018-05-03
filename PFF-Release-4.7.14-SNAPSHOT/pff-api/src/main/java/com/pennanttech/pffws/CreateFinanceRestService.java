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
	public FinanceDetail createFinance(FinanceDetail financeDetail) throws ServiceException;
	
	@POST
	@Path("/finance/createFinanceWithWIF")
	public FinanceDetail createFinanceWithWIF(FinanceDetail financeDetail) throws ServiceException;
	
	@GET
	@Path("/finance/getFinanceDetails/{finReference}")
	public FinanceDetail getFinanceDetails(@PathParam("finReference") String finReference) throws ServiceException;
	
	@GET
	@Path("/finance/getFinance/{finReference}")
	public FinanceDetail getFinInquiryDetails(@PathParam("finReference") String finReference) throws ServiceException;
	
	@GET
	@Path("/finance/getFinanceWithCustomer/{cif}")
	public FinanceInquiry getFinanceWithCustomer(@PathParam("cif") String custCif) throws ServiceException;
	
	@GET
	@Path("/finance/getFinanceWithCollateral/{collateralRef}")
	public FinanceInquiry getFinanceWithCollateral(@PathParam("collateralRef") String collateralRef) throws ServiceException;
	
	@POST
	@Path("/finance/updateLoan")
	public WSReturnStatus updateFinance(FinanceDetail financeDetail) throws ServiceException;
}
