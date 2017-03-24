package com.pennanttech.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.ws.exception.ServiceException;

public interface FinanceFlagsWebService {
	public FinanceFlag getLoanFlags(String finReference) throws ServiceException;

	public WSReturnStatus addLoanFlags(FinanceFlag financeFlag) throws ServiceException;
	
	public WSReturnStatus deleteLoanFlags(FinanceFlag financeFlag) throws ServiceException;
}
