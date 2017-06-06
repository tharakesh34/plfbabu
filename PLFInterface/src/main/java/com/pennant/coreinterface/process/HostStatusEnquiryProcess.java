package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.exception.InterfaceException;

public interface HostStatusEnquiryProcess {

	HostEnquiry getHostStatus() throws InterfaceException;

}
