package com.pennant.corebanking.process.impl;

import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.coreinterface.process.HostStatusEnquiryProcess;
import com.pennant.equation.util.DateUtility;
import com.pennanttech.pennapps.core.InterfaceException;

public class HostStatusEnquiryProcessImpl implements HostStatusEnquiryProcess {

	public HostStatusEnquiryProcessImpl() {
		
	}
	
	@Override
	public HostEnquiry getHostStatus() throws InterfaceException {

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
