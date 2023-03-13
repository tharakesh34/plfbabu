package com.pennanttech.pffws;

import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;
import com.pennanttech.ws.model.finance.EligibilitySummaryResponse;
import com.pennanttech.ws.model.miscellaneous.CheckListResponse;
import com.pennanttech.ws.model.miscellaneous.CovenantResponse;
import com.pennanttech.ws.model.miscellaneous.LoanTypeMiscRequest;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface MiscellaneousSoapService {

	WSReturnStatus createPosting(@WebParam(name = "posting") JVPosting posting) throws ServiceException;

	@WebResult(name = "DashBoardResponse")
	DashBoardResponse createDashboard(@WebParam(name = "dashboardRequest") DashBoardRequest dashboardRequest)
			throws ServiceException;

	@WebResult(name = "EligibilityDetailResponse")
	EligibilityDetailResponse createEligibilityDetail(
			@WebParam(name = "eligibilityDetail") EligibilityDetail eligibilityDetail) throws ServiceException;

	@WebResult(name = "CheckListResponse")
	List<CheckListResponse> getCheckList(
			@WebParam(name = "loanTypeMiscRequest") LoanTypeMiscRequest loanTypeMiscRequest) throws ServiceException;

	@WebResult(name = "finReference")
	CovenantResponse getCovenantDocs(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "EligibilitySummaryResponse")
	EligibilitySummaryResponse getEligibility(
			@WebParam(name = "loanTypeMiscRequest") LoanTypeMiscRequest loanTypeMiscRequest) throws ServiceException;

	@WebResult(name = "EligibilityDetailResponse")
	EligibilityDetailResponse checkEligibility(
			@WebParam(name = "eligibilityDetail") EligibilityDetail eligibilityDetail) throws ServiceException;

	@WebResult(name = "EmployerDetail")
	EmployerDetail getEmployerDetail(@WebParam(name = "eligibilityDetail") EmployerDetail employerDetail)
			throws ServiceException;

}
