package com.pennanttech.pffws;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.finance.EmiResponse;

@Produces("application/json")
public interface FinanceScheduleRestService {

	@POST
	@Path("/loanSchedule/createLoanSchedule")
	public FinScheduleData createFinanceSchedule(FinScheduleData schdData) throws ServiceException;

	@GET
	@Path("/loanSchedule/getLoanInquiry/{finReference}")
	public FinScheduleData getFinanceInquiry(@PathParam("finReference") String finReference) throws ServiceException;

	@POST
	@Path("/loanSchedule/getEMIAmount")
	public EmiResponse getEMIAmount(FinScheduleData schdData) throws ServiceException;

}
