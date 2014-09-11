package com.pennant.coreinterface.service;

import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.model.HostEnquiry;

public interface HostStatusEnquiryProcess {

	HostEnquiry getHostStatus() throws EquationInterfaceException;

}
