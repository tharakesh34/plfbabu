package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;

@Produces("application/json")
public interface MiscellaneousRestService {
	
	@POST
	@Path("/miscellaneous/createFinance")
	public WSReturnStatus createFinancePosting(JVPosting posting) throws ServiceException;
	
	@POST
	@Path("/miscellaneous/createDashboard")
	public DashBoardResponse createDashboard(@PathParam("dashboardRequest") DashBoardRequest dashboardRequest) throws ServiceException;

}
