package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.LoanPendingDetails;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.collection.CollectionAccountDetails;
import com.pennanttech.ws.model.collection.CollectionAccountReq;
import com.pennanttech.ws.model.covenantStatus.CovenantStatus;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.eligibility.AgreementData;
import com.pennanttech.ws.model.finance.DisbRequest;
import com.pennanttech.ws.model.finance.FinAdvPaymentDetail;
import com.pennanttech.ws.model.finance.ReceiptTransaction;

@Produces("application/json")
public interface FinServiceInstRESTService {

	/***
	 * Method for process add disbursement request received from API.
	 * 
	 * @param fsi
	 * @return
	 * @throws ServiceException
	 */
	@POST
	@Path("/loanInstructionService/addRateChangeRequest")
	public FinanceDetail addRateChange(FinServiceInstruction fsi) throws ServiceException;

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

	@POST
	@Path("/loanInstructionService/feePayment")
	public FinanceDetail feePayment(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/changeGestationPeriod")
	public FinanceDetail changeGestationPeriod(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@GET
	@Path("/loanInstructionService/getLoanPendingDetailsByUserName/{userName}")
	public LoanPendingDetails getLoanPendingDetailsByUserName(@PathParam("userName") String userName)
			throws ServiceException;

	@GET
	@Path("/loanInstructionService/getGSTDetails/{finReference}")
	public FinanceTaxDetail fetchGSTDetails(@PathParam("finReference") String finReference) throws ServiceException;

	@POST
	@Path("/loanInstructionService/addGSTDetails")
	public WSReturnStatus addGSTDetails(FinanceTaxDetail financeTaxDetail) throws ServiceException;

	@POST
	@Path("/loanInstructionService/updateGSTDetails")
	public WSReturnStatus updateGSTDetails(FinanceTaxDetail financeTaxDetail) throws ServiceException;

	@POST
	@Path("/loanInstructionService/approveDisbursementResponse")
	public WSReturnStatus approveDisbursementResponse(DisbRequest disbRequest) throws ServiceException;

	@GET
	@Path("/loanInstructionService/getDisbursmentDetails/{finReference}")
	public FinAdvPaymentDetail getDisbursmentDetails(@PathParam("finReference") String finReference)
			throws ServiceException;

	@POST
	@Path("/loanInstructionService/updateCovenants")
	public WSReturnStatus updateCovenants(FinanceDetail financeDetail) throws ServiceException;

	@GET
	@Path("/loanInstructionService/getChequeDetails/{finReference}")
	public ChequeHeader getChequeDetails(@PathParam("finReference") String finReference);

	@POST
	@Path("/loanInstructionService/chequeDetailsMaintainence")
	public WSReturnStatus saveChequeDetails(FinanceDetail financeDetail);

	@POST
	@Path("/loanInstructionService/createChequeDetails")
	public WSReturnStatus createChequeDetails(FinanceDetail financeDetail);

	@POST
	@Path("/loanInstructionService/updateChequeDetailsInMaintainence")
	public WSReturnStatus updateChequeDetailsInMaintainence(FinanceDetail financeDetail);

	@POST
	@Path("/loanInstructionService/updateChequeDetails")
	public WSReturnStatus updateChequeDetails(FinanceDetail financeDetail);

	@POST
	@Path("/loanInstructionService/cancelDisbursementInstructions")
	public WSReturnStatus cancelDisbursementInstructions(FinServiceInstruction finServiceInstRequest)
			throws ServiceException;

	@POST
	@Path("/loanInstructionService/restructuring")
	public FinanceDetail restructuring(RestructureDetail restructureDetail) throws ServiceException;

	@POST
	@Path("/loanInstructionService/partCancellation")
	public FinanceDetail partCancellation(FinServiceInstruction finServiceInstruction) throws ServiceException;

	@POST
	@Path("/loanInstructionService/nonLanReceipt")
	public FinanceDetail nonLanReceipt(FinServiceInstruction finServiceInstRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/subventionKnockOff")
	public WSReturnStatus subventionKnockOff(SubventionHeader subventionHead) throws ServiceException;

	@GET
	@Path("/loanInstructionService/getCovenantDocumentStatus/{finReference}")
	public List<CovenantStatus> getCovenantDocumentStatus(@PathParam("finReference") String finReference)
			throws ServiceException;

	@POST
	@Path("/loanInstructionService/getCovenantAggrement")
	public AgreementData getCovenantAggrement(AgreementRequest agreementRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/processFeeWaiver")
	public WSReturnStatus processFeeWaiver(FeeWaiverHeader feeWaiverHeader) throws ServiceException;

	@POST
	@Path("/loanInstructionService/reqCashierEntry")
	public WSReturnStatus reqCashierEntry(CollectionAccountReq collectionAccountReq) throws ServiceException;

	@POST
	@Path("/loanInstructionService/reqCashDeposit")
	public WSReturnStatus reqCashDeposit(CollectionAccountReq collectionAccountReq) throws ServiceException;

	@POST
	@Path("/loanInstructionService/saveCollectionDetais")
	public WSReturnStatus saveCollectionDetais(CollectionAccountDetails collectionAccountDetails)
			throws ServiceException;

	@POST
	@Path("/loanInstructionService/updateUTRNum")
	public WSReturnStatus updateUTRNum(ReceiptTransaction transactionRefUpdateRequest) throws ServiceException;

	@POST
	@Path("/loanInstructionService/changeRepaymentMethod")
	WSReturnStatus changeRepaymentMethod(FinanceMain financeMain) throws ServiceException;

}
