package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface QueryModuleSoapService {
	public WSReturnStatus updateQueryRequest(@WebParam(name = "queryDetail") QueryDetail queryDetail)
			throws ServiceException;

}
