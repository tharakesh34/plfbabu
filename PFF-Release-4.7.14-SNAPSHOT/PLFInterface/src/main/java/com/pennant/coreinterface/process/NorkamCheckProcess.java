package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennanttech.pennapps.core.InterfaceException;

public interface NorkamCheckProcess {
	
	InterfaceNorkamCheck doNorkamProcess(InterfaceNorkamCheck interfaceNorkamCheck) throws InterfaceException;

}
