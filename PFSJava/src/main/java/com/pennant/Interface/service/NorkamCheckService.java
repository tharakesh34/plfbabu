package com.pennant.Interface.service;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.exception.PFFInterfaceException;

public interface NorkamCheckService {
	
	InterfaceNorkamCheck doNorkamCheck(InterfaceNorkamCheck interfaceNorkamCheck) throws PFFInterfaceException;

}
