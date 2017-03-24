package com.pennanttech.service;

import org.jaxen.JaxenException;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.ws.exception.ServiceException;

public interface FinanceScheduleWebService {

	public FinScheduleData createFinanceSchedule(FinScheduleData finScheduleData) 
			throws JaxenException, PFFInterfaceException,ServiceException;
	
	public FinScheduleData getFinanceInquiry(String finReference)throws JaxenException, PFFInterfaceException;

}
