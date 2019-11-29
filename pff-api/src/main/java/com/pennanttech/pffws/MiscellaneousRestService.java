package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;
import com.pennanttech.ws.model.miscellaneous.CheckListResponse;
import com.pennanttech.ws.model.miscellaneous.CovenantResponse;
import com.pennanttech.ws.model.miscellaneous.LoanTypeMiscRequest;

@Produces("application/json")
public interface MiscellaneousRestService {

	@POST
	@Path("/miscellaneous/AddFinanceJVPostings")
	WSReturnStatus createPosting(JVPosting posting) throws ServiceException;

	@POST
	@Path("/miscellaneous/createDashboard")
	DashBoardResponse createDashboard(DashBoardRequest dashboardRequest) throws ServiceException;

	@POST
	@Path(value = "/miscellaneous/CreateEligibility")
	EligibilityDetailResponse createEligibilityDetail(EligibilityDetail eligibilityDetail) throws ServiceException;

	@GET
	@Path(value = "/miscellaneous/getCheckList")
	List<CheckListResponse> getCheckList(LoanTypeMiscRequest loanTypeMiscRequest) throws ServiceException;
	
	@GET
	@Path("/miscellaneous/getCovenants/{finReference}")
	CovenantResponse getCovenantDocs(@PathParam("finReference") String finReference) throws ServiceException;

}
