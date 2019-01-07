package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;

@WebService
public interface MiscellaneousSoapService {
	
	public WSReturnStatus createFinancePosting(@WebParam(name ="posting") JVPosting posting) throws ServiceException;
	
	public DashBoardResponse createDashboard(@WebParam(name = "dashboardRequest") DashBoardRequest dashboardRequest) throws ServiceException;

}
