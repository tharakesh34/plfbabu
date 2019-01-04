package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface MiscellaneousRestService {
	
	@POST
	@Path("/miscellaneous/createFinance")
	public WSReturnStatus createFinancePosting(JVPosting posting) throws ServiceException;

}
