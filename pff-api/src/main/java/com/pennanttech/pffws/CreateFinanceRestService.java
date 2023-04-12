package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.web.bind.annotation.RequestBody;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinReqParams;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LoanStage;
import com.pennant.backend.model.finance.UserActions;
import com.pennant.backend.model.finance.UserPendingCasesResponse;
import com.pennant.backend.model.loanauthentication.LoanAuthentication;
import com.pennant.backend.model.paymentmode.PaymentMode;
import com.pennant.backend.model.perfios.PerfiosTransaction;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.activity.ActivityLogDetails;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.deviation.DeviationList;
import com.pennanttech.ws.model.eligibility.AgreementDetails;
import com.pennanttech.ws.model.finance.FinanceDedupDetails;
import com.pennanttech.ws.model.finance.FinanceDedupResponse;
import com.pennanttech.ws.model.finance.FinanceStatusEnquiryDetail;
import com.pennanttech.ws.model.finance.LoanStatus;
import com.pennanttech.ws.model.finance.LoanStatusDetails;
import com.pennanttech.ws.model.finance.MoveLoanStageRequest;
import com.pennanttech.ws.model.financetype.FinanceInquiry;

import jakarta.jws.WebParam;

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
	FinanceInquiry getFinanceWithCollateral(@PathParam("collateralRef") String collateralRef) throws ServiceException;

	@POST
	@Path("/finance/updateLoan")
	WSReturnStatus updateFinance(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/approveLoan")
	WSReturnStatus approveLoan(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/rejectLoan")
	WSReturnStatus rejectFinance(FinanceDetail financeDetail) throws ServiceException;

	@GET
	@Path("/finance/getPendingFinanceWithCustomer/{cif}")
	FinanceInquiry getPendingFinanceWithCustomer(@PathParam("cif") String custCif) throws ServiceException;

	@POST
	@Path("/finance/cancelLoan")
	FinanceDetail cancelFinance(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/reinitiateLoan")
	FinanceDetail reInitiateFinance(FinanceDetail financeDetail) throws ServiceException;

	@POST
	@Path("/finance/moveLoanStage")
	WSReturnStatus moveLoanStage(MoveLoanStageRequest moveLoanStageRequest) throws ServiceException;

	@POST
	@Path("/finance/getLoansStatus")
	List<LoanStatus> getLoansStatus(LoanStatusDetails loanStatusDetails) throws ServiceException;

	@POST
	@Path("/finance/getAgreements")
	AgreementDetails getAgreements(AgreementRequest agreementRequest) throws ServiceException;

	@GET
	@Path("/finance/getActivityLogs/{finReference}")
	ActivityLogDetails getActivityLogs(@PathParam("finReference") String finReference) throws ServiceException;

	@GET
	@Path("/finance/getUserActions/{finReference}")
	UserActions getUserActions(@PathParam("finReference") String finReference) throws ServiceException;

	@POST
	@Path("/finance/getLoansByStage")
	UserPendingCasesResponse getLoansByStage(LoanStage loanStage) throws ServiceException;

	@GET
	@Path("/finance/getDeviations/{finReference}")
	DeviationList getDeviations(@PathParam("finReference") String finReference) throws ServiceException;

	@POST
	@Path("/finance/getLoanDeviations")
	DeviationList getLoanDeviations(FinanceDeviations financeDeviations) throws ServiceException;

	@POST
	@Path("/finance/UpdateLoanDeviation")
	WSReturnStatus updateLoanDeviation(FinanceDeviations financeDeviations) throws ServiceException;

	@POST
	@Path("/finance/updatePerfiosStatus")
	WSReturnStatus updatePerfiosStatus(PerfiosTransaction perfiosTransaction) throws ServiceException;

	@POST
	@Path("/finance/getDetailsByOfferID")
	FinCustomerDetails getDetailsByOfferID(@RequestBody String offerID) throws ServiceException;

	@POST
	@Path("/finance/financeDedup")
	FinanceDedupResponse loanDedup(FinanceDedupDetails financeDedupDetails) throws ServiceException;

	@POST
	@Path("/finance/getLoansStatusEnquiry")
	FinanceStatusEnquiryDetail getLoansStatusEnquiry(FinanceStatusEnquiryDetail financeStatusEnquiryDetail)
			throws ServiceException;

	@POST
	@Path("/finance/getFinanceDetailsByParams")
	public FinanceInquiry getFinanceDetailsByParams(FinReqParams reqParams) throws ServiceException;

	@GET
	@Path("/finance/getFinDetailsByFinReference/{finReference}")
	public FinanceDetail getFinDetailsByFinReference(@PathParam("finReference") String finReference)
			throws ServiceException;

	@GET
	@Path("/finance/getFinanceByName/{custShrtName}")
	List<FinanceDetail> getByCustShrtName(@PathParam("custShrtName") String shrtName);

	@GET
	@Path("/finance/getFinanceByPAN/{custCRCPR}")
	List<FinanceDetail> getByPANNumber(@PathParam("custCRCPR") String custCRCPR);

	@GET
	@Path("/finance/getFinanceByAccNumber/{accNumber}")
	List<FinanceDetail> getByAccountNumber(@PathParam("accNumber") String accNumber);

	@GET
	@Path("/finance/getFinanceByMobileNumber/{phoneNumber}")
	List<FinanceDetail> getByPhoneNumber(@PathParam("phoneNumber") String phoneNumber);

	@POST
	@Path("/finance/getFinanceByNameAndMobileNumber")
	List<FinanceDetail> getByCustShrtNameAndPhoneNumber(@WebParam(name = "customer") Customer customer);

	@POST
	@Path("/finance/getFinanceByNameAndDateOfBirth")
	List<FinanceDetail> getByCustShrtNameAndDateOfBirth(@WebParam(name = "customer") Customer customer);

	@POST
	@Path("/finance/getFinanceByNameAndEMIAmount")
	List<FinanceDetail> getFinIdsByNameAndEMIAmount(@WebParam(name = "financeDetail") FinanceMain financeMain);

	@POST
	@Path("/finance/loanAuthentication")
	LoanAuthentication getAuthenticationDetails(
			@WebParam(name = "loanAuthentication") LoanAuthentication loanAuthentication);

	@GET
	@Path("/finance/getPDCEnquiry/{finReference}")
	List<PaymentMode> getPDCEnquiry(@PathParam("finReference") String finReference);

	@GET
	@Path("/finance/getPDCDetails/{finReference}")
	List<PaymentMode> getPDCDetails(@PathParam("finReference") String finReference);

}
