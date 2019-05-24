package com.pennant.Interface.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.Interface.service.HostStatusEnquiryService;
import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.coreinterface.process.HostStatusEnquiryProcess;

public class HostStatusEnquiryServiceImpl implements HostStatusEnquiryService {

	protected HostStatusEnquiryProcess hostStatusEnquiryProcess;

	public HostEnquiry getHostStatus() throws Exception {
		if (hostStatusEnquiryProcess != null) {
			return hostStatusEnquiryProcess.getHostStatus();
		}
		return null;
	}

	public HostStatusEnquiryServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/*
	 * public HostStatusEnquiryProcess getHostStatusEnquiryProcess() { return hostStatusEnquiryProcess; }
	 */

	@Autowired(required = false)
	public void setHostStatusEnquiryProcess(HostStatusEnquiryProcess hostStatusEnquiryProcess) {
		this.hostStatusEnquiryProcess = hostStatusEnquiryProcess;
	}
}
