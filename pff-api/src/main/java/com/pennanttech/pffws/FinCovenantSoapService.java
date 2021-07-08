package com.pennanttech.pffws;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.finance.FinCovenantResponse;

@WebService
public interface FinCovenantSoapService {

	@WebResult(name = "finance")
	public WSReturnStatus addFinCovenant(@WebParam(name = "financeDetail") FinanceDetail financeDetail)
			throws ServiceException;

	@WebResult(name = "financeDetail")
	WSReturnStatus updateFinCovenant(@WebParam(name = "financeDetail") FinanceDetail financeDetail)
			throws ServiceException;

	@WebResult(name = "financeDetail")
	WSReturnStatus deleteFinCovenant(@WebParam(name = "financeDetail") FinanceDetail financeDetail)
			throws ServiceException;

	@WebResult(name = "finance")
	FinCovenantResponse getFinCovenants(@WebParam(name = "finReference") String finReference) throws ServiceException;

}
