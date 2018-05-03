package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.ws.exception.ServiceException;

@Produces("application/json")
public interface FeePostingRestService {
	@POST
	@Path("/feePostingService/feePosting")
	public WSReturnStatus doFeePostings(FeePostings feePostings) throws ServiceException;
}
