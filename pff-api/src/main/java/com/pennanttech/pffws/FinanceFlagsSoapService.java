package com.pennanttech.pffws;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.ws.exception.ServiceException;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService
public interface FinanceFlagsSoapService {
	@WebResult(name = "flags")
	public FinanceFlag getLoanFlags(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "flags")
	public WSReturnStatus addLoanFlags(FinanceFlag financeFlag) throws ServiceException;

	@WebResult(name = "flags")
	public WSReturnStatus deleteLoanFlags(FinanceFlag financeFlag) throws ServiceException;
}
