package com.pennant.Interface.service.impl;

import com.pennant.Interface.service.HostStatusEnquiryService;
import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.coreinterface.process.HostStatusEnquiryProcess;

public class HostStatusEnquiryServiceImpl implements HostStatusEnquiryService {

	protected HostStatusEnquiryProcess hostStatusEnquiryProcess;

	public HostEnquiry getHostStatus() throws Exception {

		return getHostStatusEnquiryProcess().getHostStatus();
	}
	
	public HostStatusEnquiryServiceImpl(){
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public HostStatusEnquiryProcess getHostStatusEnquiryProcess() {
		return hostStatusEnquiryProcess;
	}
	public void setHostStatusEnquiryProcess(HostStatusEnquiryProcess hostStatusEnquiryProcess) {
		this.hostStatusEnquiryProcess = hostStatusEnquiryProcess;
	}
}
