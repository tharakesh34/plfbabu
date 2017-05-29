package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.exception.PFFInterfaceException;

public interface NorkamCheckProcess {
	
	InterfaceNorkamCheck doNorkamProcess(InterfaceNorkamCheck interfaceNorkamCheck) throws PFFInterfaceException;

}
