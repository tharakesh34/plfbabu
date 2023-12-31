package com.pennanttech.pffws;

import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pennant.backend.model.finance.ForeClosureResponse;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;

@Produces("application/json")
public interface FinStatementRestService {
	@POST
	@Path("/statementService/getStatementofAccount")
	public FinStatementResponse getStatementOfAccount(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getInterestCerificate")
	public FinStatementResponse getInterestCertificate(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getRepaymentSchedule")
	public FinStatementResponse getRepaymentSchedule(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getNOC")
	public FinStatementResponse getNOC(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getForeclosureLetter")
	public FinStatementResponse getForeclosureLetter(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getStatement")
	public FinStatementResponse getStatement(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getStatementOfAcc")
	public StatementOfAccount getStatementOfAcc(FinStatementRequest statementRequest)
			throws ServiceException, IllegalAccessException, InvocationTargetException;

	@POST
	@Path("/statementService/getForeclosureStmt")
	public ForeClosureResponse getForeclosureStmt(FinStatementRequest statementRequest) throws ServiceException;

	@POST
	@Path("/statementService/getForeclosureStmtV1")
	public FinStatementResponse getForeclosureStmtV1(FinStatementRequest statementRequest) throws ServiceException;
}
