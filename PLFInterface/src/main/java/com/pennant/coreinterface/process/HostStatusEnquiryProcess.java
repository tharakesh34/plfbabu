package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.HostEnquiry;
import com.pennanttech.pff.core.InterfaceException;

public interface HostStatusEnquiryProcess {

	HostEnquiry getHostStatus() throws InterfaceException;

}
