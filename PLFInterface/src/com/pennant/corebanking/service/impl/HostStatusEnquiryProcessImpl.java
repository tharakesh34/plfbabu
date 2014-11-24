package com.pennant.corebanking.service.impl;

import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.coreinterface.service.HostStatusEnquiryProcess;
import com.pennant.equation.util.DateUtility;

public class HostStatusEnquiryProcessImpl implements HostStatusEnquiryProcess {

	@Override
	public HostEnquiry getHostStatus() throws EquationInterfaceException {

		// Response Data
		HostEnquiry hostEnquiry = new HostEnquiry();
		hostEnquiry.setUnitName("PFF");
		hostEnquiry.setStatusCode("CORE");
		hostEnquiry.setStatusDesc("Core System");
		hostEnquiry.setNextBusDate(DateUtility.getTodayDateTime());
		hostEnquiry.setPrevBusDate(DateUtility.getTodayDateTime());
		hostEnquiry.setCurBusDate(DateUtility.getTodayDateTime());

		return hostEnquiry;
	}

}
