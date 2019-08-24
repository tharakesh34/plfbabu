package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.transactionMapping.TransactionMappingRequest;

@WebService
public interface TransactionMappingSoapervice {
	public WSReturnStatus doAuthentication(
			@WebParam(name = "transactionMappingRequest") TransactionMappingRequest transactionMappingRequest)
			throws ServiceException;

	public WSReturnStatus doMobileAuthentication(
			@WebParam(name = "transactionMappingRequest") TransactionMappingRequest transactionMappingRequest)
			throws ServiceException;

}
