package com.pennanttech.pff.foreclosure.service;

import com.pennant.backend.model.finance.ForeClosureLetter;
import com.pennant.backend.model.finance.ForeClosureResponse;
import com.pennanttech.ws.model.statement.FinStatementRequest;

public interface ForeClosureService {

	public ForeClosureLetter getForeClosureAmt(FinStatementRequest statementReq, ForeClosureResponse response);
}
