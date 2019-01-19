package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;

@Produces("application/json")
public interface MiscellaneousRestService {
	
	@POST
	@Path("/miscellaneous/createPosting")
	public WSReturnStatus createPosting(JVPosting posting) throws ServiceException;
	
	@POST
	@Path("/miscellaneous/createDashboard")
	public DashBoardResponse createDashboard(DashBoardRequest dashboardRequest) throws ServiceException;

	@POST
	@Path(value = "/miscellaneous/CreateEligibility")
	public EligibilityDetailResponse createEligibilityDetail(EligibilityDetail eligibilityDetail)
			throws ServiceException;

}
