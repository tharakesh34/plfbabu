package com.pennant.Interface.service;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennanttech.pennapps.core.InterfaceException;

public interface NorkamCheckService {
	
	InterfaceNorkamCheck doNorkamCheck(InterfaceNorkamCheck interfaceNorkamCheck) throws InterfaceException;

}
