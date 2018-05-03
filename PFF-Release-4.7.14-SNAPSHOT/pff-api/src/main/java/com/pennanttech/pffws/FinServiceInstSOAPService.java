package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface FinServiceInstSOAPService {
	
	@WebResult(name = "finance")
	public FinanceDetail addRateChange(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail changeRepayAmt(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail deferments(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail addTerms(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail manualPayment(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
	@WebResult(name = "finance")
	public FinanceDetail removeTerms(@WebParam(name = "finance") FinServiceInstruction finServiceInstRequest) throws ServiceException;
	
}
