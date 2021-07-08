package com.pennanttech.pffws;

import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface QueryModuleSoapService {
	public WSReturnStatus updateQueryRequest(@WebParam(name = "queryDetail") QueryDetail queryDetail)
			throws ServiceException;

}
