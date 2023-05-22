package com.pennanttech.pffws;

import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinReqParams;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.LoanStage;
import com.pennant.backend.model.finance.UserActions;
import com.pennant.backend.model.finance.UserPendingCasesResponse;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.activity.ActivityLogDetails;
import com.pennanttech.ws.model.customer.AgreementRequest;
import com.pennanttech.ws.model.deviation.DeviationList;
import com.pennanttech.ws.model.eligibility.AgreementDetails;
import com.pennanttech.ws.model.finance.LoanStatus;
import com.pennanttech.ws.model.finance.LoanStatusDetails;
import com.pennanttech.ws.model.finance.MoveLoanStageRequest;
import com.pennanttech.ws.model.financetype.FinanceInquiry;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface CreateFinanceSoapService {

	@WebResult(name = "finance")
	FinanceDetail createFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;

	@WebResult(name = "finance")
	FinanceDetail createFinanceWithWIF(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;

	@WebResult(name = "finReference")
	FinanceDetail getFinanceDetails(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "finance")
	FinanceDetail getFinInquiryDetails(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "finance")
	FinanceInquiry getFinanceWithCustomer(@WebParam(name = "cif") String custCif) throws ServiceException;

	@WebResult(name = "finance")
	FinanceInquiry getFinanceWithCollateral(@WebParam(name = "collateralRef") String collateralRef)
			throws ServiceException;

	@WebResult(name = "finance")
	WSReturnStatus updateFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;

	@WebResult(name = "finance")
	WSReturnStatus rejectFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;

	@WebResult(name = "finance")
	FinanceInquiry getPendingFinanceWithCustomer(@WebParam(name = "cif") String custCif) throws ServiceException;

	@WebResult(name = "finance")
	FinanceDetail cancelFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;

	@WebResult(name = "finance")
	FinanceDetail reInitiateFinance(@WebParam(name = "finance") FinanceDetail financeDetail) throws ServiceException;

	@WebResult(name = "finance")
	WSReturnStatus moveLoanStage(@WebParam(name = "finance") MoveLoanStageRequest moveLoanStageRequest)
			throws ServiceException;

	@WebResult(name = "finance")
	List<LoanStatus> getLoansStatus(@WebParam(name = "finance") LoanStatusDetails loanStatusDetails)
			throws ServiceException;

	@WebResult(name = "finance")
	AgreementDetails getAgreements(@WebParam(name = "finance") AgreementRequest agreementRequest)
			throws ServiceException;

	@WebResult(name = "finReference")
	ActivityLogDetails getActivityLogs(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "finance")
	UserActions getUserActions(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "finance")
	UserPendingCasesResponse getLoansByStage(@WebParam(name = "finance") LoanStage loanStage) throws ServiceException;

	@WebResult(name = "finReference")
	DeviationList getDeviations(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "finance")
	FinCustomerDetails getDetailsByOfferID(@WebParam(name = "offerID") String offerID) throws ServiceException;

	@WebResult(name = "finance")
	FinanceInquiry getFinanceDetailsByParams(@WebParam(name = "finance") FinReqParams reqParams)
			throws ServiceException;

	@WebResult(name = "finance")
	public FinanceDetail getFinDetailsByFinReference(@WebParam(name = "finReference") String finReference)
			throws ServiceException;

}
