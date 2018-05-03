package com.pennanttech.pffws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.ws.exception.ServiceException;

@WebService
public interface FinanceScheduleSoapService {
	
	@WebResult(name = "financeSchedule")
	public FinScheduleData createFinanceSchedule(@WebParam(name = "financeSchedule") FinScheduleData finScheduleData) throws ServiceException;
	
	@WebResult(name = "financeSchedule")
	public FinScheduleData getFinanceInquiry(@WebParam(name = "finReference") String finReference) throws ServiceException;
	
}
