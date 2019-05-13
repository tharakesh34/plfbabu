package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;

@WebService
public interface MiscellaneousSoapService {
	
	WSReturnStatus createPosting(@WebParam(name = "posting") JVPosting posting) throws ServiceException;
	
	@WebResult(name = "DashBoardResponse")
	DashBoardResponse createDashboard(@WebParam(name = "dashboardRequest") DashBoardRequest dashboardRequest) throws ServiceException;

	@WebResult(name = "EligibilityDetailResponse")
	EligibilityDetailResponse createEligibilityDetail(
			@WebParam(name = "eligibilityDetail") EligibilityDetail eligibilityDetail) throws ServiceException;

}
