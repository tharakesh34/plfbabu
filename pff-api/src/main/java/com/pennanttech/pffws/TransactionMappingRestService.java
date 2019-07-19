package com.pennanttech.pffws;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.transactionMapping.TransactionMappingRequest;
@Produces("application/json")
public interface TransactionMappingRestService {
	@POST
	@Path("/transactionMappingService/doAuthentication")
	public WSReturnStatus doAuthentication(TransactionMappingRequest transactionMappingRequest) throws ServiceException;
}
