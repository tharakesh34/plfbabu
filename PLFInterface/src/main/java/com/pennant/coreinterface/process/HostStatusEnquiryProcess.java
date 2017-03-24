package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.exception.PFFInterfaceException;

public interface HostStatusEnquiryProcess {

	HostEnquiry getHostStatus() throws PFFInterfaceException;

}
