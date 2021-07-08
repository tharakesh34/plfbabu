package com.pennanttech.pffws;

import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.ws.model.finance.EmiResponse;

@WebService
public interface FinanceScheduleSoapService {

	@WebResult(name = "financeSchedule")
	public FinScheduleData createFinanceSchedule(@WebParam(name = "financeSchedule") FinScheduleData finScheduleData)
			throws ServiceException;

	@WebResult(name = "financeSchedule")
	public FinScheduleData getFinanceInquiry(@WebParam(name = "finReference") String finReference)
			throws ServiceException;

	@WebResult(name = "financeSchedule")
	public EmiResponse getEMIAmount(FinScheduleData finScheduleData) throws ServiceException;

}
