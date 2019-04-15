package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.manualAdvice.ManualAdviseResponse;

@Produces("application/json")
public interface FeePostingRestService {
	@POST
	@Path("/feePostingService/feePosting")
	public WSReturnStatus doFeePostings(FeePostings feePostings) throws ServiceException;

	@POST
	@Path("/feePostingService/createAdvise")
	public ManualAdviseResponse createAdvise(ManualAdvise advise) throws ServiceException;
}
