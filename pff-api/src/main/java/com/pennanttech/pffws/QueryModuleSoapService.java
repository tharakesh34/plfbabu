package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface QueryModuleSoapService {
	public WSReturnStatus updateQueryRequest(@WebParam(name = "queryDetail") QueryDetail queryDetail)
			throws ServiceException;

}
