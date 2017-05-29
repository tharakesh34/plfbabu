package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface FinanceFlagsSoapService {
	@WebResult(name = "flags")
	public FinanceFlag getLoanFlags(@WebParam(name = "finReference") String finReference) throws ServiceException;

	@WebResult(name = "flags")
	public WSReturnStatus addLoanFlags(FinanceFlag financeFlag) throws ServiceException;

	@WebResult(name = "flags")
	public WSReturnStatus deleteLoanFlags(FinanceFlag financeFlag) throws ServiceException;
}
