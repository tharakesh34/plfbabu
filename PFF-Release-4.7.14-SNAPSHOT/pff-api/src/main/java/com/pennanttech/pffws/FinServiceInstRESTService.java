package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface FinServiceInstRESTService {

	@POST
	@Path("/loanInstructionService/addRateChangeRequest")
	public FinanceDetail addRateChange(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/changeRepaymentAmountRequest")
	public FinanceDetail changeRepayAmt(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/defermentsRequest")
	public FinanceDetail deferments(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/addTermsRequest")
	public FinanceDetail addTerms(FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@POST
	@Path("/loanInstructionService/removeTermsRequest")
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/recalculate")
	public FinanceDetail recalculate(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/changeInterest")
	public FinanceDetail changeInterest(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/addDisbursement")
	public FinanceDetail addDisbursement(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/partialSettlement")
	public FinanceDetail partialSettlement(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/earlySettlement")
	public FinanceDetail earlySettlement(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/changeInstallmentFrq")
	public FinanceDetail changeInstallmentFrq(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/reScheduling")
	public FinanceDetail reScheduling(FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@POST
	@Path("/loanInstructionService/manualPayment")
	public FinanceDetail manualPayment(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/updateLoanBasicDetails")
	public WSReturnStatus updateLoanBasicDetails(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/updateLoanPenaltyDetails")
	public WSReturnStatus updateLoanPenaltyDetails(FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@POST
	@Path("/loanInstructionService/scheduleMethodChange")
	public FinanceDetail scheduleMethodChange(FinServiceInstruction finServiceInstRequest) throws ServiceException;


}
