package com.pennanttech.pffws;

import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;

@WebService
public interface FinStatementSoapService {
	@WebResult(name = "statement")
	public FinStatementResponse getStatementOfAccount(FinStatementRequest statementRequest) throws ServiceException;

	@WebResult(name = "statement")
	public FinStatementResponse getInterestCertificate(FinStatementRequest statementRequest) throws ServiceException;

	@WebResult(name = "statement")
	public FinStatementResponse getRepaymentSchedule(FinStatementRequest statementRequest) throws ServiceException;

	@WebResult(name = "statement")
	public FinStatementResponse getNOC(FinStatementRequest statementRequest) throws ServiceException;
	
	@WebResult(name = "statement")
	public FinStatementResponse getForeclosureLetter(FinStatementRequest statementRequest) throws ServiceException;
	
	@WebResult(name = "statement")
	public FinStatementResponse GetStatement(FinStatementRequest statementRequest) throws ServiceException;

}
